<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { PAYMENT_TYPES } from '@/constants';
  import { useRoute, useRouter } from 'vue-router';
  import { ref, onMounted } from 'vue';
  import paymentMethodAudio from '@/assets/audio/payment-method.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();
  const audio = ref(new Audio(paymentMethodAudio));
  const audioStore = useAudioStore();

  const handleCardClick = () => {
    router.push({
      name: 'put-payment',
      query: { task: route.query.task, payment: PAYMENT_TYPES.CARD },
    });
  };

  const handleBankbookClick = () => {
    router.push({
      name: 'put-payment',
      query: { task: route.query.task, payment: PAYMENT_TYPES.BANKBOOK },
    });
  };

  onMounted(() => {
    audioStore.initAudio(paymentMethodAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
</script>

<template>
  <ReplayAudioButton :src="paymentMethodAudio" />
  <StopAudioButton />
  <main class="w-screen h-screen flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">카드나 통장을 가져오셨나요?</p>
    <div class="justify-center gap-x-5">
      <img src="@/assets/bank-card.png" alt="카드나 통장" class="w-102 h-auto m-auto" />
    </div>
    <div class="w-screen h-32 flex flex-row justify-center pb-10 gap-10 px-10">
      <TaskButton text="카드" class="w-full max-w-sm" @click="handleCardClick" />
      <TaskButton text="통장" class="w-full max-w-sm" @click="handleBankbookClick" />
    </div>
  </main>
</template>
