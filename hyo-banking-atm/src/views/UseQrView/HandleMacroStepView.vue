<!--
Route usage:
router.push({ name: 'handle-macro-step', query: { code } })

Add to router:
{
  path: '/handle-macro-step',
  name: 'handle-macro-step',
  component: () => import('@/views/HandleMacroStep.vue')
}
-->

<template>
  <main class="min-h-screen w-full flex items-center justify-center bg-gray-50 px-4">
    <div class="w-full max-w-3xl">
      <!-- 헤더 -->
      <div class="mb-6 text-center">
        <h1 class="text-4xl font-bold">매크로 실행</h1>
        <p class="text-gray-500 mt-2">세션ID: {{ sessionId || '준비중...' }}</p>
      </div>

      <!-- 진행 바 -->
      <div class="mb-6">
        <div class="flex justify-between text-sm text-gray-500 mb-1">
          <span>단계 {{ currentIndex + 1 }} / {{ plan.length }}</span>
          <span>{{ Math.round(progress) }}%</span>
        </div>
        <div class="h-2 w-full bg-gray-200 rounded">
          <div
            class="h-2 bg-kb-yellow-200 rounded transition-all duration-300"
            :style="{ width: progress + '%' }"
          ></div>
        </div>
      </div>

      <!-- 현재 단계 카드 -->
      <div class="bg-white shadow rounded-2xl p-6">
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-bold">{{ currentTitle }}</h2>
          <span class="px-3 py-1 rounded-full text-sm" :class="statusBadgeClass">{{
            statusText
          }}</span>
        </div>
        <p class="text-gray-600 mt-3 whitespace-pre-line">{{ currentDesc }}</p>

        <!-- 액션 영역 -->
        <div class="mt-6">
          <!-- 1) 현금 수취 -->
          <div v-if="is('AWAIT_CASH')" class="space-y-4">
            <p class="text-lg">
              필요 현금 합계: <b>{{ formatWon(requiredCash) }}</b>
            </p>
            <div class="flex gap-2">
              <input
                v-model.number="countedCash"
                type="number"
                min="0"
                class="flex-1 px-4 py-3 rounded-xl border text-xl"
                placeholder="투입된 금액(원)"
              />
              <button
                class="px-6 py-3 rounded-xl bg-kb-brown-200 text-white text-xl font-semibold hover:bg-kb-brown-300"
                @click="confirmCash"
              >
                입금 완료
              </button>
            </div>
            <p v-if="cashWarning" class="text-red-500">{{ cashWarning }}</p>
            <p class="text-sm text-gray-400">* 장비 연동 시 계수 금액은 자동으로 채워집니다.</p>
          </div>

          <!-- 2) 입금 전표 처리 -->
          <div v-else-if="is('POST_DEPOSITS')" class="space-y-4">
            <ul class="divide-y border rounded-xl">
              <li
                v-for="(a, i) in allocations"
                :key="'dep-' + i"
                class="p-4 flex items-center justify-between"
              >
                <div>
                  <p class="font-semibold">{{ a.accountAlias || a.accountId }}</p>
                  <p class="text-gray-500 text-sm">입금: {{ formatWon(a.amount) }}</p>
                </div>
                <span class="text-sm" :class="statusClass(a.status)">
                  {{ a.statusLabel }}
                </span>
              </li>
            </ul>

            <div class="flex gap-2">
              <button
                class="flex-1 px-6 py-3 rounded-xl bg-kb-brown-200 text-white text-xl font-semibold hover:bg-kb-brown-300"
                :disabled="posting"
                @click="postDeposits"
              >
                전표 처리 시작
              </button>
              <button class="px-6 py-3 rounded-xl border text-xl" @click="skipStep">
                건너뛰기
              </button>
            </div>
            <p v-if="postError" class="text-red-500">{{ postError }}</p>
          </div>

          <!-- 3) 비밀번호 입력 (출금/차감 전) -->
          <div v-else-if="is('PIN_REQUIRED')" class="space-y-4">
            <p class="text-lg">출금/차감 거래가 있어 비밀번호 4자리가 필요합니다.</p>
            <input
              v-model="pin"
              type="password"
              maxlength="4"
              class="w-full px-4 py-3 rounded-xl border text-2xl tracking-widest text-center"
              placeholder="••••"
              @keyup.enter="confirmPIN"
            />
            <div class="flex gap-2">
              <button
                class="flex-1 px-6 py-3 rounded-xl bg-kb-brown-200 text-white text-xl font-semibold hover:bg-kb-brown-300"
                @click="confirmPIN"
              >
                확인
              </button>
              <button class="px-6 py-3 rounded-xl border text-xl" @click="cancelPIN">취소</button>
            </div>
            <p v-if="pinError" class="text-red-500">{{ pinError }}</p>
          </div>

          <!-- 4) 출금 처리 -->
          <div v-else-if="is('WITHDRAW')" class="space-y-4">
            <ul class="divide-y border rounded-xl">
              <li
                v-for="(w, i) in withdrawals"
                :key="'wd-' + i"
                class="p-4 flex items-center justify-between"
              >
                <div>
                  <p class="font-semibold">{{ w.accountAlias || w.accountId }}</p>
                  <p class="text-gray-500 text-sm">출금: {{ formatWon(w.amount) }}</p>
                </div>
                <span class="text-sm" :class="statusClass(w.status)">{{ w.statusLabel }}</span>
              </li>
            </ul>
            <div class="flex gap-2">
              <button
                class="flex-1 px-6 py-3 rounded-xl bg-kb-brown-200 text-white text-xl font-semibold hover:bg-kb-brown-300"
                :disabled="posting"
                @click="postWithdrawals"
              >
                출금 처리 시작
              </button>
              <button class="px-6 py-3 rounded-xl border text-xl" @click="skipStep">
                건너뛰기
              </button>
            </div>
            <p v-if="postError" class="text-red-500">{{ postError }}</p>
          </div>

          <!-- 5) 통장 업데이트(선택) -->
          <div v-else-if="is('PASSBOOK_UPDATE')" class="space-y-4">
            <p class="text-lg">
              통장 업데이트 대상:
              <b>{{ currentAction.meta?.accountAlias || currentAction.meta?.accountId }}</b>
            </p>
            <p class="text-gray-600">해당 통장을 넣어 인쇄/갱신을 완료해주세요. (건너뛰기 가능)</p>
            <div class="flex gap-2">
              <button
                class="flex-1 px-6 py-3 rounded-xl bg-kb-brown-200 text-white text-xl font-semibold hover:bg-kb-brown-300"
                @click="completePassbookUpdate"
              >
                통장 갱신 완료
              </button>
              <button class="px-6 py-3 rounded-xl border text-xl" @click="skipStep">
                건너뛰기
              </button>
            </div>
          </div>

          <!-- 6) 완료 -->
          <div v-else-if="is('DONE')" class="space-y-4 text-center">
            <p class="text-2xl font-semibold">거래가 모두 완료되었습니다.</p>
            <p class="text-gray-600">영수증 출력 또는 처음 화면으로 돌아갈 수 있습니다.</p>
            <div class="mt-4 flex gap-2 justify-center">
              <button
                class="px-6 py-3 rounded-xl bg-kb-brown-200 text-white text-xl font-semibold hover:bg-kb-brown-300"
                @click="printReceipt"
              >
                명세표 출력
              </button>
              <button class="px-6 py-3 rounded-xl border text-xl" @click="goHome">처음으로</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 푸터 액션 -->
      <div class="mt-6 flex justify-between">
        <button class="px-4 py-2 rounded-xl border" :disabled="currentIndex === 0" @click="prev">
          이전
        </button>
        <button
          v-if="canNext"
          class="px-6 py-3 rounded-xl bg-gray-900 text-white font-semibold"
          @click="next"
        >
          다음
        </button>
      </div>

      <!-- 에러 배너 -->
      <div
        v-if="fatalError"
        class="mt-4 p-4 bg-red-50 border border-red-200 text-red-700 rounded-xl"
      >
        {{ fatalError }}
      </div>
    </div>
  </main>
</template>

<script setup>
  import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getQrToken } from '@/apis';
  import { getMacroExecution, getMacroSteps, startMacroExecution } from '@/apis/macro';

  /** ===== 라우터/세션 ===== */
  const route = useRoute();
  const router = useRouter();
  const code = route.query.code?.toString() || null;
  const sessionId = ref(null);
  const macroId = ref(null);

  /** ===== 서버에서 받아올 원본 스텝 =====
   * 예상 포맷:
   * { id, type: 'DEPOSIT'|'WITHDRAW'|'TRANSFER', method: 'CASH'|'ACCOUNT',
   *   currency: 'KRW', amount, accountId, accountAlias, direction?: 'IN'|'OUT', needsPassbookUpdate?: boolean }
   */
  const rawSteps = ref([]);

  /** ===== 실행 계획(plan) =====
   * kind: 'AWAIT_CASH' | 'POST_DEPOSITS' | 'PIN_REQUIRED' | 'WITHDRAW' | 'PASSBOOK_UPDATE' | 'DONE'
   */
  const plan = ref([]);
  const currentIndex = ref(0);
  const currentAction = computed(() => plan.value[currentIndex.value] || {});
  const progress = computed(() =>
    plan.value.length ? ((currentIndex.value + 1) / plan.value.length) * 100 : 0
  );
  const canNext = computed(() => {
    const k = currentAction.value?.kind;
    return (
      k === 'AWAIT_CASH' ||
      k === 'POST_DEPOSITS' ||
      k === 'PIN_REQUIRED' ||
      k === 'WITHDRAW' ||
      k === 'PASSBOOK_UPDATE'
    );
  });

  /** ===== 표시용 텍스트 ===== */
  const currentTitle = computed(() => {
    switch (currentAction.value?.kind) {
      case 'AWAIT_CASH':
        return '현금 입금';
      case 'POST_DEPOSITS':
        return '입금 전표 처리';
      case 'PIN_REQUIRED':
        return '비밀번호 확인';
      case 'WITHDRAW':
        return '출금 처리';
      case 'PASSBOOK_UPDATE':
        return '통장 업데이트';
      case 'DONE':
        return '완료';
      default:
        return '준비 중';
    }
  });
  const currentDesc = computed(() => {
    const k = currentAction.value?.kind;
    if (k === 'AWAIT_CASH')
      return `이번 매크로에 필요한 현금 합계는 ${formatWon(requiredCash.value)} 입니다.\nATM에 현금을 넣어주세요.`;
    if (k === 'POST_DEPOSITS') return '계좌별 입금 전표를 순차 처리합니다.';
    if (k === 'PIN_REQUIRED') return '출금/차감 거래가 있어 비밀번호 입력이 필요합니다.';
    if (k === 'WITHDRAW') return '출금 전표를 순차 처리합니다.';
    if (k === 'PASSBOOK_UPDATE')
      return '해당 통장을 넣어 인쇄/갱신을 완료해주세요. (건너뛰기 가능)';
    if (k === 'DONE') return '모든 단계가 완료되었습니다.';
    return '로딩 중...';
  });
  const statusText = computed(() => currentAction.value?.state ?? '대기');
  const statusBadgeClass = computed(() => {
    const s = statusText.value;
    if (s === '완료') return 'bg-green-100 text-green-700';
    if (s === '오류') return 'bg-red-100 text-red-700';
    if (s === '진행 중') return 'bg-blue-100 text-blue-700';
    return 'bg-gray-100 text-gray-700';
  });

  /** ===== 현금 수취 ===== */
  const requiredCash = ref(0);
  const countedCash = ref(0);
  const cashWarning = ref('');
  function confirmCash() {
    cashWarning.value = '';
    if (countedCash.value <= 0) {
      cashWarning.value = '투입 금액을 입력해주세요.';
      return;
    }
    if (countedCash.value < requiredCash.value) {
      cashWarning.value = `금액이 부족합니다. (${formatWon(countedCash.value)} / ${formatWon(requiredCash.value)})`;
      return;
    }
    currentAction.value.state = '완료';
    next();
  }

  /** ===== PIN (출금/차감 전) ===== */
  const pin = ref('');
  const pinError = ref('');
  async function confirmPIN() {
    pinError.value = '';
    if (!/^\d{4}$/.test(pin.value)) {
      pinError.value = '4자리 숫자를 입력해주세요.';
      return;
    }
    try {
      // 필요시 서버에 PIN 검증 호출
      // await apiVerifyPin(sessionId.value, pin.value)
      currentAction.value.state = '완료';
      next();
    } catch (e) {
      pinError.value = '비밀번호 검증 실패';
    }
  }
  function cancelPIN() {
    pin.value = '';
    prev();
  }

  /** ===== 통장 업데이트 ===== */
  function completePassbookUpdate() {
    currentAction.value.state = '완료';
    next();
  }

  /** ===== 네비/흐름 ===== */
  function is(k) {
    return currentAction.value?.kind === k;
  }
  function next() {
    if (currentIndex.value < plan.value.length - 1) currentIndex.value++;
  }
  function prev() {
    if (currentIndex.value > 0) currentIndex.value--;
  }
  function skipStep() {
    next();
  }
  function goHome() {
    router.replace('/');
  }
  function printReceipt() {
    alert('명세표 출력(예시)');
  }

  /** ===== 초기화 ===== */
  const fatalError = ref('');
  let pollTimer = null;
  onMounted(async () => {
    try {
      if (!code) throw new Error('코드가 없습니다. 다시 스캔해주세요.');
      // 1) QR 토큰 → macroId
      const token = await getQrToken(code); // { macroId }
      macroId.value = token.macroId;

      // 2) 매크로 실행 세션 생성
      const exec = await startMacroExecution(macroId.value, code); // { executionId }
      sessionId.value = exec.id;

      // 3) 스텝 조회
      rawSteps.value = await getMacroSteps(macroId.value);

      // 4) 플랜 컴파일
      compilePlan(rawSteps.value);
      // 5) (선택) 상태 폴링 시작
      startPolling();
    } catch (e) {
      console.error(e);
      fatalError.value = e.message || '초기화 실패';
    }
  });
  onBeforeUnmount(() => {
    if (pollTimer) clearInterval(pollTimer);
  });

  function compilePlan(steps) {
    // (a) 현금 입금 스텝 (KRW & CASH) 합산
    const cashDeps = steps.filter(
      s => s.type === 'DEPOSIT' && s.method === 'CASH' && s.currency === 'KRW'
    );
    requiredCash.value = cashDeps.reduce((acc, s) => acc + (s.amount || 0), 0);

    // (b) 계좌별 분배 계획
    const depMap = new Map(); // accountId -> amount
    cashDeps.forEach(s => depMap.set(s.accountId, (depMap.get(s.accountId) || 0) + s.amount));
    allocations.value = Array.from(depMap.entries()).map(([accountId, amount]) => ({
      accountId,
      accountAlias: steps.find(x => x.accountId === accountId)?.accountAlias,
      amount,
      status: 'PENDING',
      statusLabel: '대기',
    }));

    // (c) 출금/차감 스텝 수집 (WITHDRAW + TRANSFER: OUT)
    const wd = steps.filter(
      s => s.type === 'WITHDRAW' || (s.type === 'TRANSFER' && s.direction === 'OUT')
    );
    withdrawals.value = wd.map(s => ({
      accountId: s.accountId,
      accountAlias: s.accountAlias,
      amount: s.amount,
      status: 'PENDING',
      statusLabel: '대기',
    }));

    // (d) 통장 업데이트 대상
    const passbookTargets = steps.filter(s => s.needsPassbookUpdate === true);

    // (e) 실행 시퀀스 구성 (+먼저, -나중)
    const seq = [];
    if (requiredCash.value > 0) {
      seq.push({ kind: 'AWAIT_CASH', state: '대기' });
      seq.push({ kind: 'POST_DEPOSITS', state: '대기' });
    }
    if (withdrawals.value.length > 0) {
      seq.push({ kind: 'PIN_REQUIRED', state: '대기' });
      seq.push({ kind: 'WITHDRAW', state: '대기' });
    }
    passbookTargets.forEach(s =>
      seq.push({
        kind: 'PASSBOOK_UPDATE',
        state: '대기',
        meta: { accountId: s.accountId, accountAlias: s.accountAlias },
      })
    );
    seq.push({ kind: 'DONE', state: '완료' });
    plan.value = seq;
  }

  function startPolling() {
    // 예시: 실행 상태를 주기적으로 확인
    pollTimer = setInterval(async () => {
      if (!sessionId.value) return;
      try {
        await getMacroExecution(sessionId.value); // { state, progress ... }
        // 필요 시 UI 반영
      } catch (e) {
        console.warn('poll error', e);
      }
    }, 1500);
  }

  /** ===== 유틸/API ===== */
  function formatWon(n) {
    try {
      return Number(n).toLocaleString('ko-KR') + '원';
    } catch {
      return n + '원';
    }
  }
</script>

<style scoped>
  /* 추가적인 애니메이션/전환이 필요하면 여기에 작성 */
</style>
