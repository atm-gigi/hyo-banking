<script setup>
  import { computed } from 'vue';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import TaskButton from '@/components/TaskButton.vue';
  import { useRouter } from 'vue-router';

  // 스토어 훅을 호출하여 인스턴스를 생성합니다.
  const atmStore = atmTransactionStore();
  const router = useRouter();

  console.log('atmStore in StatementView:', atmStore);
  console.log('atmStore.task in StatementView:', atmStore.task);

  const bankNameMapping = {
    KB: '국민은행',
    SHINHAN: '신한은행',
    WOORI: '우리은행',
    HANA: '하나은행',
    NH: '농협은행',
    IBK: '기업은행',
    SH: '수협은행',
    SC: '제일은행',
    BNK: '부산은행',
    DGB: '대구은행',
    GWANGJU: '광주은행',
    MG: '새마을금고',
    SHINHYUP: '신협',
  };

  // 거래 유형을 한글로 변환
  const transactionTypeDisplay = computed(() => {
    const taskMap = {
      transfer: '계좌이체및송금',
      withdraw: '현금출금',
      deposit: '현금입금',
    };
    return taskMap[atmStore.task.toLowerCase()] || '알 수 없는 거래';
  });

  // 현재 날짜와 시간을 포맷팅하는 함수
  const getCurrentDateTime = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  };

  const handleConfirmClick = () => {
    // 거래가 완료되었으므로 스토어 상태를 초기화하고 홈으로 이동
    atmStore.resetTransaction();
    router.push({ name: 'home' });
  };
</script>

<template>
  <main class="w-full h-full flex flex-col justify-center items-center bg-gray-200">
    <div class="w-full max-w-lg bg-white p-10 shadow-lg font-mono text-lg">
      <h1 class="text-3xl font-bold text-center mb-6 border-b-2 border-black pb-4">거래명세표</h1>

      <div class="space-y-2 mb-6">
        <div class="flex justify-between">
          <span class="font-semibold">거래일시:</span>
          <span>{{ getCurrentDateTime() }}</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">단말번호:</span>
          <span>ATM-001</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">거래번호:</span>
          <span>{{ atmStore.txnId || 'N/A' }}</span>
        </div>
      </div>

      <hr class="border-dashed border-black my-4" />

      <div class="space-y-2 mb-6">
        <div class="flex justify-between">
          <span class="font-semibold">거래종류:</span>
          <span class="font-bold">{{ transactionTypeDisplay }}</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">계좌번호:</span>
          <span>{{ atmStore.accountNo }}</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">거래금액:</span>
          <span class="font-bold text-xl">{{ atmStore.formattedAmount }}</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">거래후잔액:</span>
          <span>{{ atmStore.formattedBalance }}</span>
        </div>
      </div>

      <div v-if="atmStore.task === 'TRANSFER'" class="space-y-2 mb-6">
        <hr class="border-dashed border-black my-4" />
        <h2 class="text-xl font-bold mb-2">받는 분 정보</h2>
        <div class="flex justify-between">
          <span class="font-semibold">입금은행:</span>
          <span>{{ bankNameMapping[atmStore.targetBankCode] }}</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">입금계좌:</span>
          <span>{{ atmStore.targetAccountNo }}</span>
        </div>
        <div class="flex justify-between">
          <span class="font-semibold">받는분:</span>
          <span>{{ atmStore.targetUserName }}</span>
        </div>
      </div>

      <div class="text-center mt-8 border-t-2 border-black pt-4">
        <p class="text-sm">이용해주셔서 감사합니다.</p>
      </div>
    </div>

    <div class="flex pt-20">
      <TaskButton text="확인" class="w-80 h-18 bg-kb-yellow-100" @click="handleConfirmClick" />
    </div>
  </main>
</template>

<style scoped>
  /* 고전적인 영수증 느낌을 주기 위해 고정폭 폰트를 사용합니다. */
  .font-mono {
    font-family: 'Courier New', Courier, monospace;
  }
</style>
