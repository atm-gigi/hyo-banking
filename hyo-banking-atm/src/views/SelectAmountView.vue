<script setup>
  import { PAYMENT_TYPES, TASK_TYPES } from '@/constants';
  import { computed, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const amount = ref(null);

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '입금';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '출금';
    return null;
  });

  const payment = computed(() => {
    if (route.query.payment === PAYMENT_TYPES.CARD) return '카드';
    if (route.query.payment === PAYMENT_TYPES.BANKBOOK) return '통장';
    return null;
  });

  const handleAmountClick = amt => {
    amount.value = amt;
    router.push({});
  };
</script>

<template>
  <!-- TODO: 1만원 부터 100만원 까지 금액 고르는 화면 -->
  <main class="relative w-screen h-screen bg-gray-100 flex flex-row">
    <div class="w-full h-full flex flex-col gap-5 p-10">
      <button
        v-for="amount in [1, 2, 3, 4, 5, 8, 10]"
        :key="amount"
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl h-full text-4xl transition duration-300 flex items-center justify-center cursor-pointer disabled:opacity-50 disabled:active:scale-100 disabled:cursor-none"
      >
        {{ amount }}만원
      </button>
    </div>
    <div class="min-w-sm h-full bg-amber-200">2</div>
    <div class="w-full h-full flex flex-col gap-5 p-10">
      <button
        v-for="amount in [1, 2, 3, 4, 5]"
        :key="amount"
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl h-full text-4xl transition duration-300 flex items-center justify-center cursor-pointer disabled:opacity-50 disabled:active:scale-100 disabled:cursor-none"
      >
        {{ amount }}만원
      </button>
      <button
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl h-full text-4xl transition duration-300 flex items-center justify-center cursor-pointer disabled:opacity-50 disabled:active:scale-100 disabled:cursor-none"
      >
        금액 입력하기
      </button>
      <button
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl h-full text-4xl transition duration-300 flex items-center justify-center cursor-pointer disabled:opacity-50 disabled:active:scale-100 disabled:cursor-none"
      >
        거래 취소
      </button>
    </div>
  </main>
</template>
