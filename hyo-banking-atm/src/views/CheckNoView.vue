<script setup>
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';

  const route = useRoute();
  const router = useRouter();

  const handleGoBackClick = () => {
    router.push({ name: 'select-bills', query: { task: route.query.task } });
  };
  const handleQuitClick = () => {
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };
</script>

<template>
  <main class="relative h-screen flex-col justify-between p-10 bg-white">
    <p class="text-center text-5xl font-bold mb-5">거래를 취소하시겠습니까?</p>
    <img src="@/assets/cancelled.png" alt="" class="w-120 m-auto" />
    <div class="w-full flex flex-row justify-center mt-auto gap-x-5">
      <TaskButton text="아니요" class="w-full max-w-sm" @click="handleGoBackClick" />
      <TaskButton
        text="취소합니다"
        class="w-full max-w-sm bg-kb-yellow-200"
        @click="handleQuitClick"
      />
    </div>
  </main>
</template>
