<script setup>
  import { getMacroSteps, getQrToken } from '@/apis';
  import { onMounted, onUnmounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const steps = ref(null);
  const isLoading = ref(true);
  const code = route.query.code?.toString() || null;
  const totalCash = ref(0);

  const handleEnter = () => {
    console.log(steps);
    router.push({
      name: 'handle-macro-step',
      params: { steps: JSON.stringify(steps.value) },
      query: { code },
    });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    }
  };

  onMounted(async () => {
    if (!code) throw new Error('코드가 없습니다. 다시 스캔해주세요.');

    const token = await getQrToken(code);

    steps.value = await getMacroSteps(token.macroId);
    steps.value.forEach(step => {
      if (step.stepType === 'DEPOSIT' || step.stepType === 'TRANSFER')
        totalCash.value += step.amount;
      else if (step.stepType === 'WITHDRAW') totalCash.value -= step.amount;
    });
    if (totalCash.value <= 0) {
      // 입금 필요없이 바로 카드나 통장 확인으로 연결
      router.push({
        name: 'input-payment',
        params: { steps: JSON.stringify(steps.value) },
        query: { code },
      });
    }

    document.addEventListener('keydown', handleKeyPress);
    isLoading.value = false;
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });

  function formatWon(n) {
    try {
      return Number(n).toLocaleString('ko-KR') + '원';
    } catch {
      return n + '원';
    }
  }
</script>

<template>
  <main class="w-full h-full flex flex-col">
    <div
      class="w-full h-full flex flex-col gap-5 justify-center items-center p-10"
      v-if="isLoading"
    >
      <img src="@/assets/loading.png" class="w-120 m-auto" alt="곰돌이" />
      <div
        class="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin py-3"
      ></div>
      <p class="flex text-5xl text-black font-bold text-center">잠시만 기다려주세요.</p>
    </div>
    <div class="w-full h-full flex flex-col gap-5 justify-center items-center p-10" v-else>
      <p class="flex text-5xl text-black font-bold text-center">돈 넣는 곳이 열립니다.</p>
      <p class="flex text-5xl text-black font-bold text-center">
        총 필요한 금액은&nbsp;<span class="text-kb-yellow-200">{{ formatWon(totalCash) }}</span
        >이에요.
      </p>
      <span class="flex text-5xl text-black font-bold text-center"
        >지폐를 쫙 펴서 넣어주세요.
      </span>
      <img src="@/assets/put-cash.png" alt="돈 넣는 곳이 열립니다." class="w-full m-auto flex" />
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
