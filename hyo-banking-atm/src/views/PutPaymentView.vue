<script setup>
  import { PAYMENT_TYPES, TASK_TYPES } from '@/constants';
  import { computed, onMounted, onUnmounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

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
    router.push({
      name: 'ten-key',
      query: { task: route.query.task, payment: route.query.payment },
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
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col justify-between">
    <div
      class="min-w-screen bottom-0 rounded-b-r-xl rounded-b-xl bg-kb-yellow-200 text-white active:bg-kb-brown-300 font-bold py-6 px-10 text-5xl w-full max-w-md transition duration-200 flex items-center justify-center"
    >
      {{ task }}
    </div>
    <p class="text-center text-5xl leading-relaxed font-bold">
      {{ payment }}을 투입구에 넣어주세요
    </p>
    <img src="" alt="" />

    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
