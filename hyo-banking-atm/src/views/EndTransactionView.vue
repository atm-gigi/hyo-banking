<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed } from 'vue';
  import { useRoute } from 'vue-router';

  const route = useRoute();

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '돈 넣기';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '돈 찾기';
    return null;
  });
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">
      {{ task }}가 끝났습니다. <br />
      카드와 명세서를 꼭 챙겨가세요.
    </p>
    <img src="" alt="" />
  </main>
</template>
