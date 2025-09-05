<script setup>
  import { useRoute, useRouter } from 'vue-router';
  import { ref } from 'vue';

  const route = useRoute();
  const router = useRouter();

  const banks = ref([
    { name: '국민은행', logo: '' },
    { name: '신한은행', logo: '' },
    { name: '우리은행', logo: '' },
    { name: '부산은행', logo: '' },
    { name: '대구은행', logo: '' },
  ]);
  const handleClick = bank => {
    router.push({
      name: 'input-account',
      query: { task: route.query.task, payment: route.query.payment, bank: bank.name },
    });
  };
</script>
<template>
  <main class="flex flex-row h-screen bg-gray-100 p-10 gap-x-8">
    <div class="flex-1 flex flex-col items-start space-y-4">
      <button
        v-for="bank in banks"
        :key="bank.name"
        @click="handleClick(bank)"
        class="w-full max-w-md bg-kb-brown-100 py-8 active:scale-95 active:bg-kb-brown-200 rounded-lg shadow flex items-center justify-center"
      >
        <div class="w-full h-full flex justify-center">
          <img :src="bank.logo" :alt="심볼" class="w-10 h-10 m-1" />
          <p class="font-semibold text-3xl">{{ bank.name }}</p>
        </div>
      </button>
    </div>

    <div class="flex flex-col items-start justify-center">
      <p class="text-5xl font-bold text-black">돈 받으실 분의<br />은행을 골라주세요</p>
    </div>
  </main>
</template>
