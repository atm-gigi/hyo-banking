<script setup>
import { onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const handleEnter = () => {
  router.push({
    name: 'end-transaction',
    query: { task: route.query.task, payment: route.query.payment },
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
      잠시만 기다려주세요
    </p>
    <img src="" alt="" />
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
