<script setup>
  import { useAuthStore } from '../stores/auth';
  import { useRouter } from 'vue-router';
  import { maskSensitiveInfo } from '../utils/auth';
  import { formatDate } from '../utils/formatters';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import IconBtn from '@/components/buttons/IconBtn.vue';
  import { ref, onMounted } from 'vue';
  import { STORAGE_KEYS, TASK_TYPES, ICON_URLS } from '@/constants';

  const authStore = useAuthStore();
  const router = useRouter();
  const recentTransactions = ref([]);

  const handleDepositeClick = () => {
    router.push({ name: 'create-deposit' });
  };

  const handleWithdrawClick = () => {
    router.push({ name: 'create-withdraw' });
  };

  const handleTransferClick = () => {
    router.push({ name: 'create-transfer' });
  };

  const handleTransactionClick = transactionId => {
    router.push({ name: 'transaction-detail', params: { id: transactionId } });
  };

  const loadRecentTransactions = () => {
    const transactions = JSON.parse(localStorage.getItem(STORAGE_KEYS.TRANSACTIONS) || '[]');
    recentTransactions.value = transactions.slice(0, 5); // 최근 5개만 표시
  };

  const getTransactionIcon = type => {
    switch (type) {
      case TASK_TYPES.DEPOSIT:
        return ICON_URLS.MONEY_BOX;
      case TASK_TYPES.WITHDRAW:
        return ICON_URLS.MONEY;
      case TASK_TYPES.TRANSFER:
        return ICON_URLS.MONEY_TRANSFER;
      default:
        return ICON_URLS.MONEY;
    }
  };

  const getTransactionText = (type, transaction) => {
    switch (type) {
      case TASK_TYPES.DEPOSIT:
        return `계좌에 ${(transaction.amount / 10000).toLocaleString()} 만원 넣기`;
      case TASK_TYPES.WITHDRAW:
        return `${(transaction.amount / 10000).toLocaleString()} 만원 뽑기`;
      case TASK_TYPES.TRANSFER:
        if (transaction?.recipientName && transaction?.amount) {
          const amountText = `${(transaction.amount / 10000).toLocaleString()} 만원`;
          return `${transaction.recipientName}의 계좌에 ${amountText} 보내기`;
        }
        return transaction?.recipientName ? `송금 (${transaction.recipientName})` : '송금';
      default:
        return '거래';
    }
  };

  onMounted(() => {
    loadRecentTransactions();
  });

  const menuItems = [
    // {
    //   id: 'qr',
    //   text: 'QR 찍기',
    //   iconSrc: ICON_URLS.QR_CODE,
    //   iconAlt: 'qr-code--v1',
    //   handler: handleQrClick,
    // },
    {
      id: 'withdraw',
      text: '돈 뽑기',
      iconSrc: ICON_URLS.MONEY,
      iconAlt: 'money',
      handler: handleWithdrawClick,
    },
    {
      id: 'deposit',
      text: '돈 넣기',
      iconSrc: ICON_URLS.MONEY_BOX,
      iconAlt: 'money-box',
      handler: handleDepositeClick,
    },
    {
      id: 'transfer',
      text: '돈 보내기',
      iconSrc: ICON_URLS.MONEY_TRANSFER,
      iconAlt: 'initiate-money-transfer',
      handler: handleTransferClick,
    },
  ];
</script>

<template>
  <main class="relative w-full min-h-screen pb-20">
    <!-- 메인 콘텐츠 -->
    <div class="max-w-md mx-auto p-4">
      <!-- 환영 메시지 -->
      <div class="bg-white rounded-2xl p-6 mb-6 shadow-sm">
        <h2 class="text-2xl font-bold text-kb-brown-200 mb-2">
          안녕하세요, {{ authStore.userInfo?.name || '사용자' }}님
        </h2>

        <!-- 계좌 정보 -->
        <div class="rounded-lg p-4">
          <div class="text-sm text-kb-brown-200 mb-1">주계좌</div>
          <div class="font-bold text-kb-brown-200">
            {{ maskSensitiveInfo(authStore.userInfo?.accountNumber, 'account') }}
          </div>
          <div class="text-sm text-kb-gray-100">
            {{ authStore.userInfo?.bankName || '국민은행' }}
          </div>
        </div>
        <div>
          <PrimaryBtn class="w-full py-2" text="주 계좌 변경하기" />
        </div>
      </div>

      <div class="flex flex-col gap-3 bg-white p-6 rounded-2xl shadow-sm mb-6">
        <h2 class="font-bold text-lg">최근 사용 거래</h2>
        <hr class="border-gray-200" />

        <div v-if="recentTransactions.length === 0" class="text-center py-8 text-gray-500">
          <p>최근 사용한 거래가 없습니다.</p>
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="transaction in recentTransactions"
            :key="transaction.id"
            @click="handleTransactionClick(transaction.id)"
            class="flex items-center justify-between py-2 cursor-pointer hover:bg-gray-50 rounded-lg p-2 transition-colors"
          >
            <div class="flex items-center gap-3">
              <img
                :src="getTransactionIcon(transaction.type)"
                :alt="getTransactionText(transaction.type, transaction)"
                class="w-8 h-8"
              />
              <div>
                <p class="font-medium text-gray-900">
                  {{ getTransactionText(transaction.type, transaction) }}
                </p>
                <p class="text-sm text-gray-500">
                  {{ formatDate(transaction.createdAt) }}
                </p>
              </div>
            </div>
            <img class="size-4" width="30" height="30" :src="ICON_URLS.FORWARD" alt="forward" />
          </div>
        </div>
      </div>

      <!-- 메뉴 버튼들 -->
      <div class="flex flex-col gap-3 bg-white p-6 rounded-2xl shadow-sm">
        <h2 class="font-bold text-lg">ATM 거래 추가하기</h2>
        <hr class="border-gray-200" />
        <template v-for="(item, index) in menuItems" :key="item.id">
          <IconBtn
            :text="item.text"
            :icon-src="item.iconSrc"
            :icon-alt="item.iconAlt"
            @click="item.handler"
          />
          <hr v-if="index < menuItems.length - 1" class="border-gray-200" />
        </template>
      </div>
    </div>
  </main>
</template>
