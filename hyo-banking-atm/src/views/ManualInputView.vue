<script setup>
  import { onMounted, onUnmounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';
  import { TASK_TYPES } from '@/constants';
  import TransactionAPI from '@/apis/TransactionAPI';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const amount = ref('');
  const task = computed(() => route.query.task);

  const title = computed(() => {
    // route.query.task가 'TRANSFER'이면 "얼마를 보내실 건가요?"를,
    // 그렇지 않으면 "얼마를 찾으실 건가요?"를 반환합니다.
    return task.value === TASK_TYPES.TRANSFER ? '얼마를 보내실 건가요?' : '얼마를 찾으실 건가요?';
  });

  const onKeyClick = key => {
    if (key === '지움') {
      amount.value = amount.value.slice(0, -1);
    } else if (key === '정정') {
      amount.value = '';
    } else {
      amount.value += key;
    }
  };

  const handleEnter = async () => {
    if (!amount.value) {
      console.log('금액을 입력해주세요.');
      return;
    }
    const transactionAmount = parseInt(amount.value) * 10000;
    atmStore.setAmount(transactionAmount); // Update store

    if (task.value === TASK_TYPES.TRANSFER) {
      try {
        // 5. Use await to wait for the API call to complete
        const response = await TransactionAPI.createTransaction({
          txnType: TASK_TYPES.TRANSFER,
          targetBankCode: atmStore.targetBankCode, // Use data from store
          targetAccountNo: atmStore.targetAccountNo, // Use data from store
          amount: transactionAmount,
          currencyCode: 'KRW',
          description: 'ATM 송금 - 강남지점',
        });

        console.log('송금 확인:', response.txnId, ' - ', amount.value, '만원');
        router.push({
          name: 'check-transfer',
          query: { ...route.query },
        });
      } catch (error) {
        console.error('Transaction error:', error);
        router.push({
          name: 'error',
          query: { message: '거래에 실패했습니다. 다시 시도해주세요.' },
        });
      }
    } else {
      // For withdrawals, just navigate to the next step
      router.push({
        name: 'input-password',
        query: { ...route.query },
      });
    }
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
  <main class="w-screen h-screen">
    <div class="flex flex-row w-full h-full rounded-lg p-5">
      <!-- 질문 -->
      <div class="w-1/2 flex flex-col space-y-5 justify-center">
        <h1 class="text-5xl font-bold text-black">{{ title }}</h1>
        <!-- 입력창 -->
        <div class="text-4xl text-center font-extrabold justify-center rounded items-center mx-10">
          <span class="items-center text-blue-500 border-r-4 border-blue-500 pr-1">{{
            amount
          }}</span>
          <span> 만원</span>
        </div>
      </div>
      <!-- 키패드 -->
      <div class="w-1/2 flex items-center justify-center">
        <KeyPad @keyClick="onKeyClick" />
      </div>
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>

<style scoped></style>
