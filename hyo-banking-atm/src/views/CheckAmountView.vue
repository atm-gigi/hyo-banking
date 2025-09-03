<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const isShowAssistantView = ref(true);

  const handleGoBackClick = () => {
    router.push({ name: 'home' });
  };

  const handleWithdrawalClick = () => {
    router.push({
      name: 'loading',
      query: { task: TASK_TYPES.WITHDRAW },
    });
  };
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col">
    <div class="flex flex-col items-center justify-between h-screen py-16 bg-white">
      <!-- 질문 텍스트 -->
      <h1 class="text-3xl font-bold text-black">10만원을 찾으시겠어요?</h1>
      <div class="w-full h-full flex flex-col px-10 justify-around">
        <TaskButton
          text="아니오"
          class="w-full max-w-sm"
          :disabled="isShowAssistantView"
          @click="handleGoBackClick"
        />
        <TaskButton
          text="예 찾을거에요"
          class="w-full max-w-sm bg-kb-yellow-200"
          :disabled="isShowAssistantView"
          @click="handleWithdrawalClick"
        />
      </div>
    </div>
  </main>
</template>
