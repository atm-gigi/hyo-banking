<script setup>
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';

  const route = useRoute();
  const router = useRouter();

  const handleGoBackClick = () => {
    router.push({name:'select-bills', query: { task: route.query.task }});
  };
  const handleQuitClick = () => {
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };

</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">거래를 취소하시겠습니까?</p>
    <img src="" alt="걱정곰돌이" />
    <div class="w-full h-32 flex flex-row justify-center px-10 gap-x-5">
      <TaskButton text="아니요" class="w-full max-w-sm" @click="handleGoBackClick" />
      <TaskButton
        text="취소합니다"
        class="w-full max-w-sm bg-kb-yellow-200"
        @click="handleQuitClick"
      />
    </div>
  </main>
</template>
