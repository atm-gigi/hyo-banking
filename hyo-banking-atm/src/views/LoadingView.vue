<script setup>
  import { onMounted, onUnmounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const task = ref(route.query.task);

  const title = computed(() => {
    if (task.value === 'transfer') {
      return '돈을 보내고 있어요';
    } else if (task.value === 'withdraw') {
      return '돈이 나오고 있어요';
    } else {
      // 'DEPOSIT' 또는 그 외의 경우
      return '돈을 계좌에 넣고 있어요';
    }
  });

  const handleEnter = () => {
    router.push({
      name: 'end-transaction',
      query: { task: route.query.task },
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
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">
      {{ title }}
    </p>
    <img src="" alt="곰돌이" />
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
