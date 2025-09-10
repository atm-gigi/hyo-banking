<template>
  <main class="min-h-screen flex items-center justify-center p-6">
    <div class="w-full max-w-4xl">
      <!-- 헤더 -->
      <header class="mb-10 flex items-center justify-between">
        <h1 class="text-4xl font-extrabold text-kb-brown-100">ATM 매크로 실행</h1>
        <span
          class="px-4 py-2 rounded-full text-lg font-semibold"
          :class="{
            'bg-gray-200 text-gray-700': status === 'PENDING',
            'bg-kb-yellow-100 text-black': status === 'RUNNING',
            'bg-green-500 text-white': status === 'SUCCEEDED',
            'bg-rose-500 text-white': status === 'FAILED',
          }"
        >
          {{ statusLabel }}
        </span>
      </header>

      <section>
        <!-- 상태 메시지 & 스피너 -->
        <div v-if="isPendingOrRunning" class="flex flex-col items-center gap-6">
          <div
            class="w-20 h-20 border-8 border-kb-yellow-100 border-t-transparent rounded-full animate-spin"
          ></div>
          <p class="text-4xl font-bold text-kb-brown-100 text-center">{{ statusMessage }}</p>
        </div>

        <!-- 성공 -->
        <div
          v-else-if="status === 'SUCCEEDED'"
          class="flex flex-col items-center gap-4 text-green-600"
        >
          <span class="text-6xl">✅</span>
          <p class="text-4xl font-bold">{{ successMessage }}</p>
        </div>

        <!-- 실패 -->
        <div v-else-if="status === 'FAILED'" class="text-center text-rose-600">
          <p class="text-4xl font-bold mb-2">거래가 실패했습니다</p>
          <p v-if="macro?.errorMessage" class="text-lg">
            {{ macro.errorMessage }}
            <span v-if="macro.errorCode">({{ macro.errorCode }})</span>
          </p>
        </div>

        <hr class="my-10" />

        <!-- 스텝 리스트 -->
        <h2 class="text-2xl font-bold mb-6 text-kb-brown-100">진행 단계</h2>
        <ol class="space-y-4">
          <li
            v-for="(s, i) in steps"
            :key="s.id ?? i"
            class="p-6 rounded-2xl border-2 transition"
            :class="
              i === (macro?.currentStep ?? -1)
                ? 'border-kb-yellow-100 bg-yellow-50'
                : 'border-gray-300 bg-gray-50'
            "
          >
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <span class="text-2xl">
                  {{ s.stepType === 'DEPOSIT' ? '📥' : s.stepType === 'WITHDRAW' ? '💸' : '🔁' }}
                </span>
                <span class="text-xl font-bold">{{ labelOf(s.stepType) }}</span>
              </div>
              <span class="text-sm text-gray-500">순서 {{ s.stepOrder }}</span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-lg">
              <!-- 계좌 정보 -->
              <div v-if="s.stepType === 'WITHDRAW'">
                <span class="block text-gray-500 text-sm">출금 계좌</span>
                <div class="font-mono">{{ s.sourceBankCode }} · {{ s.sourceAccountNo }}</div>
              </div>
              <div v-else>
                <span class="block text-gray-500 text-sm">입금 계좌</span>
                <div class="font-mono">{{ s.targetBankCode }} · {{ s.targetAccountNo }}</div>
              </div>

              <div>
                <span class="block text-gray-500 text-sm">예금주</span>
                <div>{{ users[i] || '-' }}</div>
              </div>
              <div>
                <span class="block text-gray-500 text-sm">금액</span>
                <div class="font-bold text-kb-brown-300">
                  {{ formatWon(s.amount ?? 0) }}
                </div>
              </div>

              <div>
                <span class="block text-gray-500 text-sm">비고</span>
                <div>{{ s.note || '-' }}</div>
              </div>
            </div>
          </li>
        </ol>

        <!-- 액션 버튼 -->
        <div class="mt-10 flex gap-6 justify-center">
          <button
            v-if="status === 'SUCCEEDED'"
            @click="goNext"
            class="bg-kb-brown-100 hover:bg-kb-brown-300 active:bg-kb-yellow-100 text-white active:text-black font-bold rounded-2xl text-4xl px-12 py-6 transition transform active:scale-105 shadow-lg"
          >
            다음
          </button>
          <button
            v-if="status === 'FAILED'"
            @click="goUndo"
            class="bg-gray-300 hover:bg-gray-400 active:bg-kb-yellow-200 text-black font-bold rounded-2xl text-4xl px-12 py-6 transition transform active:scale-105 shadow-lg"
          >
            되돌리기
          </button>
        </div>
      </section>
    </div>
  </main>
</template>

<script setup>
  import { getMacroExecution, getUserByAccount, startMacroExecution } from '@/apis';
  import { onMounted, onUnmounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const steps = JSON.parse(route.params.steps);
  const session = ref(null);
  const macroId = steps[0].macroId;
  let polling = null;
  const macro = ref(null);

  const status = computed(() => macro.value?.status || 'PENDING');
  const isPendingOrRunning = computed(
    () => status.value === 'PENDING' || status.value === 'RUNNING'
  );
  const statusLabel = computed(
    () =>
      ({ PENDING: '대기 중', RUNNING: '진행 중', SUCCEEDED: '완료', FAILED: '실패' })[status.value]
  );

  // 금액 합계
  const withdrawTotal = computed(() =>
    steps.filter(s => s.stepType === 'WITHDRAW').reduce((a, b) => a + Number(b.amount || 0), 0)
  );
  const depositTotal = computed(() =>
    steps.filter(s => s.stepType === 'DEPOSIT').reduce((a, b) => a + Number(b.amount || 0), 0)
  );
  const hasWithdraw = computed(() => withdrawTotal.value > 0);
  const users = ref([]);

  const statusMessage = computed(() =>
    status.value === 'PENDING' ? '거래 대기 중입니다…' : '거래가 진행 중입니다…'
  );
  const successMessage = computed(() =>
    hasWithdraw.value
      ? '현금을 수령해 주세요 💸'
      : depositTotal.value > 0
        ? '입금이 완료되었습니다.'
        : '거래가 완료되었습니다.'
  );

  function formatWon(n) {
    try {
      return Number(n).toLocaleString('ko-KR') + '원';
    } catch {
      return `${n}원`;
    }
  }

  function labelOf(type) {
    switch (type) {
      case 'DEPOSIT':
        return '입금';
      case 'WITHDRAW':
        return '출금';
      case 'TRANSFER':
        return '이체';
      default:
        return type;
    }
  }

  onMounted(async () => {
    const qrToken = route.query.code?.toString() || null;

    steps.forEach(step => {
      if (step.stepType === 'DEPOSIT' || step.stepType === 'TRANSFER')
        getUsers(step.targetAccountNo, step.targetBankCode);
      else if (step.stepType === 'WITHDRAW') getUsers(step.sourceAccountNo, step.sourceBankCode);
    });
    const res = await startMacroExecution(macroId, qrToken);
    if (res && res.id) {
      session.value = res;
      startPolling();
    } else {
      console.error('세션 생성 실패', res);
      router.push({ name: 'undo-transaction', query: { task: '매크로' } });
    }
  });
  const getUsers = async (accountNo, bankCode) => {
    const user = await getUserByAccount(accountNo, bankCode);
    users.value.push(user.name);
  };

  const startPolling = () => {
    polling = setInterval(async () => {
      console.log('실행 세션 아이디' + session.value.id);
      macro.value = await getMacroExecution(session.value.id);

      switch (macro.value.status) {
        case 'PENDING':
          console.log('대기 중...');
          break;
        case 'RUNNING':
          console.log('진행 중...');
          break;
        case 'SUCCEEDED':
          console.log('성공! 현금을 수령하세요 💸');
          clearInterval(polling);
          // 필요하다면 다음 화면으로 이동
          //   router.push({ name: 'handle-withdraw', query: { amount: amount.value } });
          break;
        case 'FAILED':
          console.error('실패 ㅠㅠ', macro.value.errorMessage);
          clearInterval(polling);
          router.push({ name: 'undo-transaction', query: { task: 'macro' } });
          break;
      }
    }, 1000);
  };

  onUnmounted(() => {
    if (polling) clearInterval(polling);
  });

  function goNext() {
    // 둘 다 없다면 영수증/완료 화면 등
    router.push({ name: 'end-transaction', query: { task: 'macro' } });
  }

  function goUndo() {
    router.push({ name: 'undo-transaction', query: { task: 'macro' } });
  }
</script>
