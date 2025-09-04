<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { PAYMENT_TYPES, TASK_TYPES } from '@/constants';
  import { computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '입금';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '출금';
    return null;
  });

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
  <main class="relative h-screen bg-gray-100 flex flex-col justify-between">
    <div
      class="min-w-screen bottom-0 rounded-b-r-xl rounded-b-xl bg-kb-yellow-200 text-white active:bg-kb-brown-300 font-bold py-6 px-10 text-5xl w-full max-w-md transition duration-200 flex items-center justify-center"
    >
      {{ task }}
    </div>
    <p class="text-center text-5xl leading-relaxed font-bold">카드나 통장을 가져오셨나요?</p>
    <img src="" alt="카드나 통장사진" />
    <div class="w-screen h-32 flex flex-row justify-center pb-10 gap-10 px-10">
      <TaskButton text="카드" class="w-full max-w-sm" @click="handleCardClick" />
      <TaskButton text="통장" class="w-full max-w-sm" @click="handleBankbookClick" />
    </div>
  </main>
</template>
