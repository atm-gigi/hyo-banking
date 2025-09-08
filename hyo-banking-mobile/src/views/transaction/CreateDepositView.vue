<script setup lang="ts">
  import TextInput from '@/components/TextInput.vue';
  import NumberPad from '@/components/NumberPad.vue';
  import { ref } from 'vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';
  import { TASK_TYPES, STORAGE_KEYS } from '@/constants';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const amount = ref('');
  const showNumberPad = ref(false);
  const showModal = ref(false);
  const showAmountError = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  const handleAmountClick = () => {
    showNumberPad.value = true;
  };

  const handleNumberPadConfirm = value => {
    amount.value = value;
    showNumberPad.value = false;
  };

  const handleNumberPadCancel = () => {
    showNumberPad.value = false;
  };

  const handleAmountSelect = value => {
    amount.value = value;
  };

  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  const handleAddTransaction = () => {
    if (!amount.value || amount.value === '0') {
      showAmountError.value = true;
      // 3초 후 에러 메시지 숨기기
      setTimeout(() => {
        showAmountError.value = false;
      }, 3000);
      return;
    }

    // 거래 정보 생성
    const transaction = {
      id: Date.now().toString(),
      type: TASK_TYPES.DEPOSIT,
      amount: parseInt(amount.value) * 10000, // 만원을 원으로 변환
      createdAt: new Date().toISOString(),
    };

    // 기존 거래 목록 가져오기
    const existingTransactions = JSON.parse(
      localStorage.getItem(STORAGE_KEYS.TRANSACTIONS) || '[]'
    );

    // 새 거래 추가
    existingTransactions.unshift(transaction);

    // 로컬스토리지에 저장
    localStorage.setItem(STORAGE_KEYS.TRANSACTIONS, JSON.stringify(existingTransactions));

    showAlert('거래 완료', '입금 거래가 추가되었습니다.');
  };

  const handleModalConfirm = () => {
    showModal.value = false;
    // 홈으로 이동
    window.history.back();
  };

  const handleModalCancel = () => {
    showModal.value = false;
  };
</script>

<template>
  <main class="w-full h-full flex flex-col justify-between">
    <div class="px-5">
      <div class="pt-10 pb-8">
        <BackButton />
      </div>

      <h1 class="text-2xl font-bold mb-10">얼마를 넣으시겠어요?</h1>
      <div @click="handleAmountClick" class="cursor-pointer flex items-center gap-3 pb-3">
        <TextInput
          class="text-xl"
          v-model="amount"
          text="금액"
          name="amount"
          :required="true"
          type="text"
          placeholder="금액을 입력하세요"
          readonly
        />
        <span class="w-11 font-bold text-lg">만원</span>
      </div>

      <div class="flex flex-row flex-wrap gap-5">
        <button
          v-for="m in [1, 3, 5, 7, 10, 15, 20]"
          :key="`amount-${m}`"
          @click="handleAmountSelect(m)"
          class="w-20 py-3 bg-gray-200 rounded-2xl cursor-pointer hover:scale-105 active:scale-95 active:brightness-75 transition"
        >
          {{ m }} 만원
        </button>
      </div>
    </div>

    <div class="px-5 pb-10">
      <!-- 에러 메시지 -->
      <div
        v-if="showAmountError"
        class="mb-3 p-3 bg-red-100 border border-red-300 rounded-xl text-red-700 text-center text-sm"
      >
        금액을 입력해주세요
      </div>

      <PrimaryBtn class="w-full py-2" text="거래 추가" @click="handleAddTransaction" />
    </div>

    <!-- Number Pad -->
    <NumberPad
      v-model="amount"
      :isVisible="showNumberPad"
      currency="만원"
      @confirm="handleNumberPadConfirm"
      @cancel="handleNumberPadCancel"
    />

    <!-- Modal -->
    <Modal
      :isVisible="showModal"
      :title="modalConfig.title"
      :message="modalConfig.message"
      :showCancel="modalConfig.showCancel"
      @confirm="handleModalConfirm"
      @cancel="handleModalCancel"
    />
  </main>
</template>
