<script setup>
  import { onMounted, onUnmounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const handleEnter = () => {
    router.push({
      name: 'check-amount',
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
  <main class="relative w-screen h-screen flex flex-col">
    <div class="w-full h-full flex flex-col gap-5 justify-center items-center p-10">
      <p class="flex text-5xl text-black font-bold text-center">돈 넣는 곳이 열립니다.</p>
      <span class="flex text-5xl text-black font-bold text-center"
        >지폐를 쫙 펴서 넣어주세요.
      </span>
      <img src="@/assets/put-cash.png" alt="돈 넣는 곳이 열립니다." class="w-full m-auto flex" />
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
