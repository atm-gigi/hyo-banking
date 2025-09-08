<script setup>
  import { onMounted, onUnmounted, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const errorMessage = computed(() => {
    // atmStore.description에 값이 있으면 그 값을 사용하고,
    // 없으면 (null 또는 undefined) 기본 에러 메시지를 반환합니다.
    return atmStore.description ?? '오류가 발생했습니다. 다시 시도해주세요.';
  });

  const handleEnter = () => {
    router.push({
      name: 'undo-transaction',
      query: { task: route.query.task },
    });
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
  <main class="relative h-screen flex flex-col justify-between">
    <div class="flex flex-col items-center justify-center">
      <p class="p-10 flex text-center text-5xl font-bold">
        {{ errorMessage }}
      </p>
      <img src="@/assets/wrong-number.png" alt="w-102 h-auto flex" />
    </div>
    <form class="" @onKeyClick="handleKeyClick"></form>
  </main>
</template>
