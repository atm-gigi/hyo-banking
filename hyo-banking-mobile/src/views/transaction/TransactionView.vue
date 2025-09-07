<script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { STORAGE_KEYS, TASK_TYPES } from '@/constants';
  import { formatDate, formatAmount } from '@/utils/formatters';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';

  const router = useRouter();
  const route = useRoute();
  const transaction = ref(null);
  const showDeleteModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: true,
  });

  onMounted(() => {
    loadTransaction();
  });

  const loadTransaction = () => {
    const transactionId = route.params.id;
    if (!transactionId) return;

    const transactions = JSON.parse(localStorage.getItem(STORAGE_KEYS.TRANSACTIONS) || '[]');
    transaction.value = transactions.find(t => t.id === transactionId);
  };

  const handleDeleteClick = () => {
    modalConfig.value = {
      title: '거래 삭제',
      message: '정말로 이 거래를 삭제하시겠습니까?',
      showCancel: true,
    };
    showDeleteModal.value = true;
  };

  const handleDeleteConfirm = () => {
    if (!transaction.value) return;

    // 로컬스토리지에서 거래 목록 가져오기
    const transactions = JSON.parse(localStorage.getItem(STORAGE_KEYS.TRANSACTIONS) || '[]');

    // 해당 거래 제거
    const updatedTransactions = transactions.filter(t => t.id !== transaction.value.id);

    // 로컬스토리지에 저장
    localStorage.setItem(STORAGE_KEYS.TRANSACTIONS, JSON.stringify(updatedTransactions));

    // 모달 닫기
    showDeleteModal.value = false;

    // 홈으로 이동
    router.push({ name: 'home' });
  };

  const handleDeleteCancel = () => {
    showDeleteModal.value = false;
  };

  const handleQrClick = () => {
    if (transaction.value) {
      router.push({ name: 'show-qr', params: { id: transaction.value.id } });
    }
  };

  const getTransactionIcon = type => {
    switch (type) {
      case TASK_TYPES.DEPOSIT:
        return '💰';
      case TASK_TYPES.WITHDRAW:
        return '💸';
      case TASK_TYPES.TRANSFER:
        return '📤';
      default:
        return '💳';
    }
  };

  const getTransactionGradient = type => {
    switch (type) {
      case TASK_TYPES.DEPOSIT:
        return 'bg-gradient-to-br from-green-500 to-green-600';
      case TASK_TYPES.WITHDRAW:
        return 'bg-gradient-to-br from-red-500 to-red-600';
      case TASK_TYPES.TRANSFER:
        return 'bg-gradient-to-br from-blue-500 to-blue-600';
      default:
        return 'bg-gradient-to-br from-gray-500 to-gray-600';
    }
  };

  const getTransactionTitle = type => {
    switch (type) {
      case TASK_TYPES.DEPOSIT:
        return '입금';
      case TASK_TYPES.WITHDRAW:
        return '출금';
      case TASK_TYPES.TRANSFER:
        return '송금';
      default:
        return '거래';
    }
  };

  const getAmountColor = type => {
    switch (type) {
      case TASK_TYPES.DEPOSIT:
        return 'text-green-600';
      case TASK_TYPES.WITHDRAW:
      case TASK_TYPES.TRANSFER:
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  };
</script>

<template>
  <main class="w-full h-full flex flex-col">
    <!-- 헤더 -->
    <div class="px-5 pt-10 pb-6">
      <div class="mb-6">
        <BackButton />
      </div>
      <h1 class="text-2xl font-bold">거래 상세</h1>
    </div>

    <!-- 거래 상세 정보 -->
    <div v-if="transaction" class="px-5 flex-1">
      <!-- Transaction Type Card -->
      <div class="bg-white rounded-2xl shadow-sm p-6 mb-6">
        <div class="flex items-center justify-center mb-4">
          <div
            class="w-16 h-16 rounded-full flex items-center justify-center text-white font-bold text-xl mr-4"
            :class="getTransactionGradient(transaction.type)"
          >
            {{ getTransactionIcon(transaction.type) }}
          </div>
          <div class="text-center">
            <h2 class="text-xl font-bold text-gray-900">
              {{ getTransactionTitle(transaction.type) }}
            </h2>
            <p class="text-sm text-gray-500">{{ formatDate(transaction.createdAt) }}</p>
          </div>
        </div>
      </div>

      <!-- 거래 금액 -->
      <div class="bg-white rounded-2xl shadow-sm p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-700 mb-4">거래 금액</h3>
        <div class="text-center">
          <p class="text-3xl font-bold" :class="getAmountColor(transaction.type)">
            {{ formatAmount(transaction.amount) }}
          </p>
        </div>
      </div>

      <!-- 거래 상세정보 -->
      <div
        v-if="transaction.type === TASK_TYPES.TRANSFER"
        class="bg-white rounded-2xl shadow-sm p-6 mb-6"
      >
        <h3 class="text-lg font-semibold text-gray-700 mb-4">거래 정보</h3>
        <div class="space-y-4">
          <div class="space-y-3">
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="text-gray-600">받는 사람</span>
              <span class="font-medium text-gray-900">{{ transaction.recipientName }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="text-gray-600">은행</span>
              <span class="font-medium text-gray-900">{{ transaction.bankName }}</span>
            </div>
            <div class="flex justify-between items-center py-2">
              <span class="text-gray-600">계좌번호</span>
              <span class="font-medium text-gray-900">{{ transaction.accountNumber }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- No Transaction Found -->
    <div v-else class="px-5 flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-gray-500 text-lg">거래 정보를 찾을 수 없습니다.</p>
        <button
          @click="handleBack"
          class="mt-4 px-6 py-2 bg-kb-yellow-200 text-kb-brown-200 rounded-xl font-semibold"
        >
          돌아가기
        </button>
      </div>
    </div>

    <div class="px-5 flex gap-5 pb-10">
      <button
        @click="handleDeleteClick"
        class="w-full py-2 bg-red-500 text-md font-bold text-red-50 rounded-2xl hover:scale-105 active:scale-95 active:brightness-75 transition cursor-pointer"
      >
        삭제
      </button>
      <PrimaryBtn class="w-full py-2" text="QR 만들기" @click="handleQrClick" />
    </div>

    <!-- 삭제 확인 모달 -->
    <Modal
      :isVisible="showDeleteModal"
      :title="modalConfig.title"
      :message="modalConfig.message"
      :showCancel="modalConfig.showCancel"
      @confirm="handleDeleteConfirm"
      @cancel="handleDeleteCancel"
    />
  </main>
</template>
