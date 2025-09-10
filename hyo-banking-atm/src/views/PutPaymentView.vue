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
    if (route.query.payment === PAYMENT_TYPES.CARD) return '카드를';
    if (route.query.payment === PAYMENT_TYPES.BANKBOOK) return '통장을';
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

    setTimeout(() => {
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
    }, 3000);
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>

<template>
  <ReplayAudioButton :src="putPaymentAudio" />
  <StopAudioButton />
  <main class="w-full h-full flex flex-col items-center">
    <h1 class="text-5xl font-bold text-center py-10">투입구 확인 후 {{ payment }} 넣어주세요</h1>

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
        <p class="text-3xl font-semibold py-6">
          <span class="font-bold leading-relaxed">뭔가 이상한가요?</span> <br />
          절대 {{ payment }} 넣지 마시고, 바로 옆 인터폰(📞)으로 직원을 불러주세요.
        </p>
      </div>
    </div>

    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
