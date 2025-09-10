<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed, onMounted, onUnmounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const task = computed(() => {
    if ((route.query.task || atmStore.task) === TASK_TYPES.DEPOSIT) return '돈 넣기';
    if ((route.query.task || atmStore.task) === TASK_TYPES.WITHDRAW) return '돈 찾기';
    if ((route.query.task || atmStore.task) === TASK_TYPES.TRANSFER) return '돈 보내기';

    return null;
  });

  const handleEnter = () => {
    router.push({ name: 'suggest-door-phone', query: { task: route.query.task } });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    } else if (event.key === 'Backspace') {
      router.push({
        name: 'error',
        query: { task: route.query.task },
      });
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
  <main class="relative h-screen bg-white flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">
      {{ task }}가 취소되었습니다. <br />
      카드와 명세서를 꼭 챙겨가세요.
    </p>
    <img src="@/assets/take-card-bills.png" alt="" class="w-200 m-auto" />
    <form class="" @onKeyClick="handleKeyClick"></form>
  </main>
</template>
