<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { TASK_TYPES } from '@/constants';
  import TransactionAPI from '@/apis/TransactionAPI';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const amount = ref(166000);
  atmStore.setAmount(amount.value);
  const isShowAssistantView = ref(false);

  function getImg(path) {
    return new URL(`../assets/${path}`, import.meta.url).href;
  }

  const billDenominations = [
    { denomination: 50000, label: '5만원', img: '50000_won.jpg' },
    { denomination: 10000, label: '1만원', img: '10000_won.jpeg' },
    { denomination: 5000, label: '5천원', img: '5000_won.jpeg' },
    { denomination: 1000, label: '1천원', img: '1000_won.jpeg' },
  ];

  const billCounts = computed(() => {
    const counts = {};
    let remainingAmount = amount.value;

    for (const bill of billDenominations) {
      const denomination = bill.denomination;
      if (remainingAmount >= denomination) {
        counts[denomination] = Math.floor(remainingAmount / denomination);
        remainingAmount %= denomination;
      } else {
        counts[denomination] = 0;
      }
    }
    return counts;
  });

  // 금액을 통화 형식(e.g., 166,000)으로 변환
  const formattedAmount = computed(() => {
    return amount.value.toLocaleString('ko-KR');
  });

  // '예 맞아요' 버튼 클릭 시 실행될 함수
  const handleDepositClick = () => {
    const response = TransactionAPI.createTransaction({
      txnType: atmStore.taskType || TASK_TYPES.DEPOSIT,
      targetBankCode: atmStore.bankCode || 'KB',
      targetAccountNo: atmStore.accountNo || '110-123-456789',
      amount: amount.value || 166000,
      currencyCode: atmStore.currencyCode || 'KRW',
      description: 'ATM 입금 - 강남지점',
    });

    atmStore.setDescription('ATM 입금 - 강남지점');

    if (response.error) {
      console.error('Transaction error:', response.error, response.details);
      atmStore.setDescription(response.error);
      router.push({ name: 'error' });
    } else {
      router.push({
        name: 'loading',
        query: { task: route.query.task },
      });
    }
  };

  // '아니오' 버튼 클릭 시 실행될 함수
  const handleGoBackClick = () => {
    console.log('입금 취소');
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };
</script>

<template>
  <main class="relative w-screen h-screen flex flex-col">
    <div class="rounded-xl text-center text-5xl font-semibold text-black p-10">
      <p class="py-3">입금하신 금액이</p>
      <span class="text-5xl font-bold"> {{ formattedAmount }}원 </span>
      맞으신가요?
    </div>

    <div class="grid grid-cols-2 gap-x-3 gap-y-5 justify-items-center items-center my-5 space-y-10">
      <div
        v-for="bill in billDenominations"
        :key="bill.denomination"
        class="flex items-center space-x-3 w-full justify-center"
      >
        <img :src="getImg(bill.img)" :alt="bill.label" class="w-112" />
        <span class="text-5xl font-semibold w-20 text-left">
          {{ billCounts[bill.denomination] }}장
        </span>
      </div>
    </div>

    <div class="w-screen h-screen flex justify-center gap-x-5 mt-auto">
      <TaskButton
        text="아니오"
        class="w-full max-w-sm"
        :disabled="isShowAssistantView"
        @click="handleGoBackClick"
      />
      <TaskButton
        text="예 맞아요"
        class="w-full max-w-sm bg-kb-yellow-200"
        :disabled="isShowAssistantView"
        @click="handleDepositClick"
      />
    </div>
  </main>
</template>
