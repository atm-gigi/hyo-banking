<template>
  <main class="min-h-screen bg-gray-50 flex items-center justify-center p-6">
    <div class="w-full max-w-3xl">
      <!-- 헤더 -->
      <header class="mb-6 flex items-center justify-between">
        <h1 class="text-2xl font-bold">ATM 매크로 실행</h1>
        <span
          class="px-3 py-1 rounded-full text-sm font-semibold"
          :class="{
            'bg-gray-200 text-gray-700': status === 'PENDING',
            'bg-sky-100 text-sky-700': status === 'RUNNING',
            'bg-green-100 text-green-700': status === 'SUCCEEDED',
            'bg-rose-100 text-rose-700': status === 'FAILED',
          }"
        >
          {{ statusLabel }}
        </span>
      </header>

      <section class="bg-white rounded-2xl shadow p-6">
        <!-- 상태 메시지 & 스피너 -->
        <div v-if="isPendingOrRunning" class="flex items-center gap-4">
          <div
            class="w-12 h-12 border-4 border-sky-500 border-t-transparent rounded-full animate-spin"
          ></div>
          <p class="text-xl font-semibold">{{ statusMessage }}</p>
        </div>

        <!-- 성공 -->
        <div
          v-else-if="status === 'SUCCEEDED'"
          class="flex items-center gap-3 text-green-600 text-xl font-semibold"
        >
          <span class="i">✅</span>
          <span>거래가 완료되었습니다. {{ successMessage }}</span>
        </div>

        <!-- 실패 -->
        <div v-else-if="status === 'FAILED'" class="text-rose-600">
          <p class="text-xl font-semibold">거래가 실패했습니다. 다시 시도해 주세요.</p>
          <p v-if="macro?.errorMessage" class="mt-1 text-sm">
            {{ macro.errorMessage }} <span v-if="macro.errorCode">({{ macro.errorCode }})</span>
          </p>
        </div>

        <hr class="my-6" />

        <!-- 스텝 리스트 -->
        <h2 class="text-lg font-semibold mb-4">진행 단계</h2>
        <ol class="space-y-3">
          <li
            v-for="(s, i) in steps"
            :key="s.id ?? i"
            class="p-4 rounded-xl border transition"
            :class="
              i === (macro?.currentStep ?? -1)
                ? 'border-sky-400 bg-sky-50'
                : 'border-gray-200 bg-white'
            "
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span class="text-lg">
                  {{ s.stepType === 'DEPOSIT' ? '📥' : s.stepType === 'WITHDRAW' ? '💸' : '🔁' }}
                </span>
                <span class="font-semibold">{{ labelOf(s.stepType) }}</span>
              </div>
              <span class="text-sm text-gray-500">순서 {{ s.stepOrder }}</span>
            </div>

            <div class="mt-2 grid grid-cols-1 md:grid-cols-3 gap-2 text-sm">
              <!-- 계좌 정보: DEPOSIT/TRANSFER는 target, WITHDRAW는 source -->
              <div v-if="s.stepType === 'WITHDRAW'">
                <span class="text-gray-500">출금 계좌</span>
                <div class="font-mono">{{ s.sourceBankCode }} · {{ s.sourceAccountNo }}</div>
              </div>
              <div v-else>
                <span class="text-gray-500">입금 계좌</span>
                <div class="font-mono">{{ s.targetBankCode }} · {{ s.targetAccountNo }}</div>
              </div>

              <div>
                <span class="text-gray-500">금액</span>
                <div class="font-bold">
                  <span class="text-rose-600">{{ formatWon(s.amount ?? 0) }}</span>
                </div>
              </div>

              <div>
                <span class="text-gray-500">비고</span>
                <div>{{ s.note || '-' }}</div>
              </div>
            </div>
          </li>
        </ol>

        <!-- 액션 버튼 -->
        <div class="mt-6 flex gap-3 justify-end">
          <button
            v-if="status === 'SUCCEEDED'"
            class="px-5 py-3 rounded-xl bg-sky-600 text-white font-semibold hover:bg-sky-700"
            @click="goNext"
          >
            다음
          </button>
          <button
            v-if="status === 'FAILED'"
            class="px-5 py-3 rounded-xl bg-gray-200 hover:bg-gray-300"
            @click="goUndo"
          >
            되돌리기
          </button>
        </div>
      </section>
    </div>
  </main>
</template>

<script setup>
  import { getMacroExecution, startMacroExecution } from '@/apis';
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
  const amount = computed(() => withdrawTotal.value + depositTotal.value);

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
    session.value = await startMacroExecution(macroId, qrToken);
    console.log(session.value);

    startPolling();
  });

  const startPolling = () => {
    polling = setInterval(async () => {
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
          router.push({ name: 'handle-withdraw', query: { amount: amount.value } });
          break;
        case 'FAILED':
          console.error('실패 ㅠㅠ', macro.value.errorMessage);
          clearInterval(polling);
          router.push({ name: 'undo-transaction', query: { task: '매크로' } });
          break;
      }
    }, 1000);
  };

  onUnmounted(() => {
    if (polling) clearInterval(polling);
  });
</script>
