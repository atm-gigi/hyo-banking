<script setup>
  import { PAYMENT_TYPES, TASK_TYPES } from '@/constants';
  import { computed, onMounted, onUnmounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import putPaymentAudio from '@/assets/audio/put-payment.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();
  const isShowAssistantView = ref(true);
  const audio = ref(new Audio(putPaymentAudio));
  const audioStore = useAudioStore();

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '입금';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '출금';
    return null;
  });

  const payment = computed(() => {
    if (route.query.payment === PAYMENT_TYPES.CARD) return '카드';
    if (route.query.payment === PAYMENT_TYPES.BANKBOOK) return '통장';
    return null;
  });

  const handleEnter = () => {
    if (task.value === '입금') {
      router.push({
        name: 'input-cash',
        query: { task: route.query.task, payment: route.query.payment },
      });
    } else {
      router.push({
        name: 'select-amount',
        query: { task: route.query.task, payment: route.query.payment },
      });
    }
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    }
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyPress);
    audioStore.initAudio(putPaymentAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });

  onMounted(() => {
    setTimeout(() => {
      isShowAssistantView.value = false;
    }, 3000);
  });
</script>

<template>
  <ReplayAudioButton :src="putPaymentAudio" />
  <StopAudioButton />
  <main class="w-screen h-screen flex flex-col items-center p-10">
    <h1 class="text-5xl font-bold text-center mb-6">투입구를 확인해주세요</h1>

    <div class="w-full max-w-4xl bg-white rounded-2xl p-5">
      <div class="grid grid-cols-2 gap-x-8 text-center">
        <div class="border-4 border-green-400 rounded-xl p-4">
          <h2 class="text-5xl font-bold text-green-600 mb-2">✅ 정상 투입구</h2>
          <img
            src="@/assets/atm-true.png"
            alt="정상 투입구 예시"
            class="w-full h-48 object-contain"
          />
        </div>

        <div class="border-4 border-red-400 rounded-xl p-5 bg-red-50">
          <h2 class="text-5xl font-bold text-red-600 mb-2">⚠️ 의심되는 투입구</h2>
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
        <p class="text-3xl font-semibold">
          <span class="font-bold">뭔가 이상한가요?</span> <br />
          절대 카드를 넣지 마시고, 바로 옆 인터폰(📞)으로 직원을 불러주세요.
        </p>
      </div>
    </div>

    <div class="w-full text-center m-auto">
      <p class="text-5xl font-bold transition-opacity duration-1000 ease-in-out">
        확인하셨다면, 투입구에 {{ payment }}을(를) 넣어주세요
      </p>
    </div>
    <!-- <main class="relative w-screen h-screen flex flex-col justify-between">
    <div
      class="absolute inset-0 flex items-center justify-center text-center"
      :class="{
        'opacity-100 pointer-events-none': isShowAssistantView,
        'opacity-0': !isShowAssistantView,
      }"
    >
      <p class="text-5xl font-bold transition-opacity duration-3000 ease-in-out">투입구를 화면 속 불법복제기와 비교해보고<br /> 투입구에 {{ payment }} 넣어주세요</p>
    </div>

    <div
      class="flex flex-col justify-center items-center w-full h-full text-5xl"
      :class="{
        'opacity-0': isShowAssistantView,
        'opacity-100 pointer-events-none': !isShowAssistantView,
      }"
    >

        <span>만약</span> <p class="font-bold"> 불법 복제기</p>가 부착된 것으로
        의심되는 경우,<br />
        통장과 카드를 넣지 마시고<br />
        즉시 인터폰을 들어<br />
        직원에게 도움을 요청해주세요.


    </div>
 <div class="flex flex-row justify-center gap-x-5">
   <img src="@/assets/atm-card-copy.png" alt="불법복제기사진" class="flex w-102 h-auto"/>
   <img src="@/assets/prevent-card-copy.png" alt="불법복제기사진" class="flex w-88 h-auto"/>
 </div> -->
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
