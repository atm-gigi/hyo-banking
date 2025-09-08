<script setup>
  import { computed, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';
  import TransactionAPI from '@/apis/TransactionAPI';
  import UserAPI from '@/apis/UserAPI';
  import { atmTranactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTranactionStore();

  const bankCodeMapping = {
    국민은행: 'KB',
    신한은행: 'SHINHAN',
    우리은행: 'WOORI',
    하나은행: 'HANA',
    농협은행: 'NH',
    기업은행: 'IBK',
    수협은행: 'SH',
    제일은행: 'SC',
    부산은행: 'BNK',
    대구은행: 'DGB',
    광주은행: 'GWANGJU',
    새마을금고: 'MG',
    신협: 'SHINHYUP',
  };

  const receiverName = computed(() => atmStore.targetUserName || '정보 없음');
  const receiverBank = computed(() => atmStore.targetBank || '은행 정보 없음');
  const receiverAccount = computed(() => atmStore.targetAccountNo || '계좌 정보 없음');
  const receiverAmount = computed(
    () => atmStore.amount?.toLocaleString('ko-KR') + '원' || '금액 정보 없음'
  );

  onMounted(async () => {
    if (!atmStore.targetAccountNo || !atmStore.targetBankCode) {
      console.error('조회할 계좌 정보가 없습니다.');
      return;
    }

    try {
      const bankCode = bankCodeMapping[atmStore.targetBank];
      const response = await UserAPI.getUserInfo(atmStore.targetAccountNo, bankCode);
      const { userId, name } = response.data;
      atmStore.setTargetUserId(userId);
      atmStore.setTargetUserName(name);
    } catch (error) {
      console.error('계좌 정보 조회 실패:', error);
      atmStore.setTargetUserName('조회 실패');
    }
  });

  const handleNoClick = () => {
    console.log('Transaction canceled.');
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };

  const handleYesClick = async () => {
    console.log('Transaction confirmed.');
    router.push({ name: 'loading' });

    try {
      const transactionData = {
        txnType: atmStore.task,
        targetBankCode: bankCodeMapping[atmStore.targetBank], // Map bank name to code
        targetAccountNo: atmStore.targetAccountNo,
        amount: atmStore.amount, // Use the raw number from the store
        currencyCode: 'KRW',
        description: 'ATM 이체',
      };

      await TransactionAPI.createTransaction(transactionData);

      router.push({
        name: 'end-transaction',
        query: { task: route.query.task },
      });
    } catch (error) {
      console.error('Transaction failed:', error);
      router.push({ name: 'error' });
    }
  };

  const receiverDetails = computed(() => [
    { label: '은행', value: receiverBank.value },
    { label: '계좌번호', value: receiverAccount.value },
    { label: '예금주', value: receiverName.value },
    { label: '금액', value: receiverAmount },
  ]);
</script>
<template>
  <div class="p-10 h-screen w-screen flex flex-col justify-between bg-white">
    <h1 class="text-5xl font-bold text-center">이 분에게 보내는 것이 맞나요?</h1>

    <div class="flex items-center justify-center gap-8 mt-8 mb-12">
      <img src="@/assets/pointing.png" alt="bear" class="w-88 h-auto" />

      <div>
        <div class="text-5xl font-bold mb-4">{{ receiverName }}</div>

        <table class="text-left text-3xl w-full">
          <tbody>
            <tr
              v-for="detail in receiverDetails"
              :key="detail.label"
              class="border-t last:border-b"
            >
              <td class="py-2 text-gray-600 font-semibold">{{ detail.label }}</td>
              <td class="py-2 pl-4">{{ detail.value }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="w-full flex flex-row justify-center gap-x-5">
      <TaskButton text="아니요" class="w-full max-w-sm" @click="handleNoClick" />
      <TaskButton text="네" class="w-full max-w-sm bg-kb-yellow-200" @click="handleYesClick" />
    </div>
  </div>
</template>
