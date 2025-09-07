<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { PAYMENT_TYPES } from '@/constants';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

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
</script>

<template>
  <main class="w-screen h-screen flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">카드나 통장을 가져오셨나요?</p>
    <div class="flex flex-row justify-center gap-x-5">
      <img src="@/assets/card.png" alt="카드" class="flex w-88 h-auto" />
      <img src="@/assets/bank-book.png" alt="통장" class="flex w-88 h-auto" />
    </div>
    <div class="w-screen h-32 flex flex-row justify-center pb-10 gap-10 px-10">
      <TaskButton text="카드" class="w-full max-w-sm" @click="handleCardClick" />
      <TaskButton text="통장" class="w-full max-w-sm" @click="handleBankbookClick" />
    </div>
  </main>
</template>
