<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const handleGoBackClick = () => {
    router.push({ name: 'check-no' , query: { task: TASK_TYPES.WITHDRAW } });
  };

  const handleWithdrawalClick = () => {
    router.push({
      name: 'select-bills',
      query: { task: TASK_TYPES.WITHDRAW },
    });
  };
</script>

<template>
  <main class="relative h-screen bg-gray-100 justify-between flex flex-col">
    <div class="flex flex-col items-center text-center py-10">
      <!-- 질문 텍스트 -->
      <h1 class="text-5xl font-bold text-black">10만원을 찾으시겠어요?</h1>
    </div>
    <div class="w-full flex flex-row justify-center mt-auto gap-x-5">
      <TaskButton
        text="아니요"
        class="w-full max-w-sm"
        @click="handleGoBackClick"
      />
      <TaskButton
        text="네"
        class="w-full max-w-sm bg-kb-yellow-200"
        @click="handleWithdrawalClick"
      />
    </div>
  </main>
</template>
