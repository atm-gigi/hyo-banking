<script setup>
  import { ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const amount = ref(null);

  const buttons = ref([
    { type: 'amount', label: '3만원', value: 30000 },
    { type: 'amount', label: '40만원', value: 400000 },
    { type: 'amount', label: '5만원', value: 50000 },
    { type: 'amount', label: '50만원', value: 500000 },
    { type: 'amount', label: '10만원', value: 100000 },
    { type: 'amount', label: '70만원', value: 700000 },
    { type: 'amount', label: '15만원', value: 150000 },
    { type: 'amount', label: '100만원', value: 1000000 },
    { type: 'amount', label: '20만원', value: 200000 },
    {
      type: 'action',
      label: '직접 입력',
      action: 'manualInput',
      customClass: 'bg-green-600 active:bg-green-800',
    },
    { type: 'amount', label: '30만원', value: 300000 },
    {
      type: 'action',
      label: '거래 취소',
      action: 'cancel',
      customClass: 'bg-red-600 active:bg-red-800',
    },
  ]);

  const handleButtonClick = button => {
    if (button.type === 'amount') {
      // 금액 선택 로직
      console.log(`${button.label} (${button.value}원) 선택됨`);
      amount.value = button.value;
      if (route.query.task === 'transfer') {
        router.push({
          name: 'check-transfer',
          query: { task: route.query.task, amount: amount.value },
        });
      } else {
        router.push({
          name: 'input-password',
          query: { task: route.query.task },
        });
      }
    } else if (button.type === 'action') {
      // 기능 버튼 로직
      switch (button.action) {
        case 'manualInput':
          router.push({
            name: 'manual-input',
            query: { task: route.query.task },
          });
          break;
        case 'cancel':
          router.push({
            name: 'undo-transaction',
            query: { task: route.query.task },
          });
          break;
      }
    }
  };
</script>

<template>
  <main class="relative w-screen h-screen bg-gray-100 grid grid-cols-2 gap-5 p-10">
    <button
      v-for="button in buttons"
      :key="button.label"
      @click="handleButtonClick(button)"
      :class="[
        'active:scale-105 text-white font-bold rounded-xl text-4xl transition duration-300 flex items-center justify-center cursor-pointer disabled:opacity-50 disabled:active:scale-100 disabled:cursor-none',
        button.customClass ? button.customClass : 'bg-kb-brown-100 active:bg-kb-brown-300', // 기본 스타일 또는 커스텀 스타일 적용
      ]"
    >
      {{ button.label }}
    </button>
  </main>
</template>
