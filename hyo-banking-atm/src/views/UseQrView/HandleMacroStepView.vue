<template>
  <main class="w-full h-full flex items-center justify-center p-6">
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
            <div class="relative">
              <span
                class="absolute top-0 right-0 px-2 py-1 text-lg font-semibold text-blue-600 bg-blue-100 rounded-full"
                :class="tagClassesOf(s.stepType)"
              >
                {{ labelOf(s.stepType) }}
              </span>
              <div class="mb-2">
                <span class="block text-2xl font-bold text-gray-900">{{ s.note }} </span>
              </div>
              <div class="flex items-start gap-x-2">
                <span class="pt-1 text-sm text-gray-500">순서 {{ s.stepOrder }}</span>
                <span class="pt-1 text-gray-400">|</span>
                <div>
                  <p class="text-lg">
                    <span class="font-extrabold">{{ users[i] || '고객' }}</span>
                    (<span class="font-mono">
                      {{
                        s.stepType === 'WITHDRAW'
                          ? s.sourceBankCode + ' · ' + s.sourceAccountNo
                          : s.targetBankCode + ' · ' + s.targetAccountNo
                      }} </span
                    >)님에게
                    <span class="font-bold text-kb-brown-300">{{ formatWon(s.amount ?? 0) }}</span
                    >을 {{ s.stepType === 'WITHDRAW' ? '보냈습니다' : '넣었습니다' }}.
                  </p>
                </div>
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
        return '돈 넣기';
      case 'WITHDRAW':
        return '돈 꺼내기';
      case 'TRANSFER':
        return '돈 보내기';
      default:
        return type;
    }
  }

  function tagClassesOf(type) {
    // 공통으로 사용될 기본 클래스
    const baseClasses = 'absolute top-0 right-0 px-2 py-1 text-lg font-semibold rounded-full';

    let colorClasses = '';
    switch (type) {
      case 'DEPOSIT': // 돈 넣기 (입금)
        colorClasses = 'bg-red-100 text-red-600';
        break;
      case 'WITHDRAW': // 돈 꺼내기 (출금)
        colorClasses = 'bg-blue-100 text-blue-600';
        break;
      case 'TRANSFER': // 돈 보내기 (송금)
        colorClasses = 'bg-yellow-100 text-yellow-600';
        break;
      default: // 기본값
        colorClasses = 'bg-gray-100 text-gray-600';
        break;
    }

    // 기본 클래스와 색상 클래스를 합쳐서 반환
    return `${baseClasses} ${colorClasses}`;
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
