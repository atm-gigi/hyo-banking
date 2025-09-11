<script setup></script>

<template>
  <main class="w-full h-full flex flex-col items-center">
    <h1 class="text-5xl font-bold text-center py-10">투입구를 확인해주세요</h1>

    <div class="w-full max-w-4xl bg-white rounded-2xl p-5">
      <div class="grid grid-cols-2 gap-x-8 text-center">
        <div class="border-4 border-green-400 rounded-xl p-4">
          <h2 class="text-5xl font-bold text-green-600 mb-4">✅ 정상 투입구</h2>
          <img
            src="@/assets/atm-true.png"
            alt="정상 투입구 예시"
            class="w-full h-48 object-contain"
          />
        </div>

        <div class="border-4 border-red-400 rounded-xl p-5 bg-red-50">
          <h2 class="text-5xl font-bold text-red-600 mb-2">⚠️ 의심 투입구</h2>
          <div class="flex flex-row justify-center gap-x-2 mb-4">
            <img
              src="@/assets/atm-false.png"
              alt="비정상 투입구 예시"
              class="flex w-full h-48 object-contain"
            />
            <img
              src="@/assets/prevent-card-copy.png"
              alt="비정상 투입구 예시"
              class="flex w-full h-48 object-contain"
            />
          </div>
        </div>
      </div>

      <div class="mt-6 bg-yellow-100 border-l-8 border-yellow-400 p-4 rounded-md">
        <p class="text-3xl font-semibold py-1">
          <span class="font-bold leading-relaxed">뭔가 이상한가요?</span> <br />
          절대 카드나 통장을 넣지 마시고, 바로 옆 인터폰(📞)으로 직원을 불러주세요.
        </p>
      </div>
    </div>

    <div class="w-full text-center m-auto">
      <p class="text-5xl font-bold transition-opacity duration-1000 ease-in-out leading-relaxed">
        확인하셨다면, QR사용에 필요한 <br />
        카드나 통장을 넣어주세요
      </p>
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>

<script setup>
  import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const code = route.query.code?.toString() || null;
  const isShowAssistantView = ref(true);
  const steps = JSON.parse(route.params.steps);

  const targetSteps = computed(() =>
    steps.filter(step => step.stepType === 'DEPOSIT' || step.stepType === 'WITHDRAW')
  );

  const handleEnter = () => {
    router.push({
      name: 'input-password-withdraw',
      params: { steps: JSON.stringify(steps) },
      query: { code },
    });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    }
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyPress);
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });

  onMounted(() => {
    setTimeout(() => {
      isShowAssistantView.value = false;
    }, 3000);
  });

  // targetSteps가 비어 있으면 자동 이동
  watch(
    targetSteps,
    newVal => {
      console.log(newVal);
      if (newVal.length === 0) {
        router.push({
          name: 'input-password-withdraw',
          params: { steps: JSON.stringify(steps) },
          query: { code },
        });
      }
    },
    { immediate: true }
  ); // immediate: 컴포넌트 마운트될 때도 체크
</script>
