<script setup>
  import { onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  onMounted(() => {
    setTimeout(() => {
      router.push({ name: 'undo-transaction', query: { task: route.query.task } });
    }, 3000);
  });
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">
      번호 확인 후 다시 시도해주세요.
    </p>
    <img src="" alt="" />
  </main>
</template>
