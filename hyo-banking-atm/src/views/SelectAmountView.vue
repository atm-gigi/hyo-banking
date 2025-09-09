<script setup>
  import { ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { TASK_TYPES } from '@/constants';
  import TransactionAPI from '@/apis/TransactionAPI';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();
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
      customClass: 'bg-kb-yellow-200 active:bg-green-800',
    },
    { type: 'amount', label: '30만원', value: 300000 },
    {
      type: 'action',
      label: '거래 취소',
      action: 'cancel',
      customClass: 'bg-red-500 active:bg-red-800',
    },
  ]);

  const handleButtonClick = async button => {
    if (button.type === 'amount') {
      console.log(`${button.label} (${button.value}원) 선택됨`);
      atmStore.setAmount(button.value);
      amount.value = button.value;

      try {
        if (route.query.task === 'transfer') {
          // Use 'await' to wait for the API call to finish
          const response = await TransactionAPI.createTransaction({
            txnType: TASK_TYPES.TRANSFER,
            sourceBankCode: atmStore.bankCode || 'KB',
            sourceAccountNo: atmStore.accountNo || '110-123-456789',
            targetBankCode: atmStore.targetBankCode || 'SH',
            targetAccountNo: atmStore.targetAccountNo || '111-123-456789',
            amount: amount.value,
            currencyCode: 'KRW',
            description: 'ATM 송금 - 강남지점',
          });

          // Now you can safely check the response
          atmStore.setAmount(button.value);
          console.log('송금 확인:', response.txnId, ' - ', amount.value, '원');
          router.push({
            name: 'check-transfer',
            query: { task: route.query.task, amount: amount.value },
          });
        } else {
          // Use 'await' here as well
          const response = await TransactionAPI.createTransaction({
            txnType: TASK_TYPES.WITHDRAW,
            sourceBankCode: atmStore.bankCode || 'KB',
            sourceAccountNo: atmStore.accountNo || '110-123-456789',
            amount: amount.value,
            currencyCode: atmStore.currencyCode || 'KRW',
            description: 'ATM 출금 - 강남지점',
          });

          console.log('출금 확인:', response.txnId, ' - ', amount.value, '원');
          router.push({
            name: 'input-password',
            query: { task: route.query.task },
          });
        }
      } catch (error) {
        // If the promise rejects (like with a 500 error), it will be caught here
        console.error('Transaction error:', error);
        router.push({
          name: 'error',
          query: { message: 'Transaction failed. Please try again.' },
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
  <main class="relative w-screen h-screen grid grid-cols-2 gap-5 p-10">
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
