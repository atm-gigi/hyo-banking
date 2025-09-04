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
  <main class="relative h-screen bg-gray-100 flex flex-col">
    <div class="flex flex-col items-center justify-between h-screen py-16 bg-white">
      <!-- 질문 텍스트 -->
      <h1 class="text-3xl font-bold text-black">10만원을 찾으시겠어요?</h1>
      <div class="w-full h-32 flex flex-row justify-center px-10 gap-x-5">
        <TaskButton
          text="아니요"
          class="w-full max-w-sm"
          @click="handleGoBackClick"
        />
        <TaskButton
          text="네 찾을거에요"
          class="w-full max-w-sm bg-kb-yellow-200"
          @click="handleWithdrawalClick"
        />
      </div>
    </div>
  </main>
</template>
