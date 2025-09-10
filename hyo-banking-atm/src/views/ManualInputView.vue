<script setup>
  import { onMounted, onUnmounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';
  import { TASK_TYPES } from '@/constants';
  import TransactionAPI from '@/apis/TransactionAPI';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import manualInputAudio from '@/assets/audio/manual-input.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';
  import TaskButton from '@/components/TaskButton.vue';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const amount = ref('');
  const task = computed(() => route.query.task);
  const audio = ref(new Audio(manualInputAudio));
  const audioStore = useAudioStore();

  const isFull = computed(() => {
    return amount.value.length >= 1 && amount.value != 0;
  });

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

    try {
      if (route.query.task === 'transfer') {
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
      console.error('Transaction error:', error);
      router.push({
        name: 'error',
        query: { message: '거래에 실패했습니다. 다시 시도해주세요.' },
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
    audioStore.initAudio(manualInputAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>

<template>
  <ReplayAudioButton :src="manualInputAudio" />
  <StopAudioButton />
  <main class="w-full h-screen flex items-center justify-center">
    <div class="flex flex-row w-full h-full">
      <div class="w-1/2 flex flex-col justify-center items-center h-175">
        <h1 class="text-5xl text-center font-bold text-black mb-5">{{ title }}</h1>
        <!-- 입력창 -->
        <div class="text-5xl text-center font-extrabold items-center mx-10">
          <span class="items-center text-blue-500 border-r-4 border-blue-500 pr-1">{{
            amount
          }}</span>
          <span> 만원</span>
        </div>
      </div>

      <!-- 키패드 -->
      <div class="flex w-1/2 h-full pb-50">
        <KeyPad @keyClick="onKeyClick" class="w-full h-[450px]" />
      </div>
    </div>

    <div class="fixed bottom-10" :class="{ 'opacity-50 cursor-not-allowed': !isFull }">
      <TaskButton class="w-80 h-18 bg-kb-yellow-200" text="확인"> </TaskButton>
    </div>
  </main>
</template>

<style scoped></style>
