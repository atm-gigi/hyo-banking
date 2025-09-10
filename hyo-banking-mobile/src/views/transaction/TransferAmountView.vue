<script setup lang="ts">
  import TextInput from '@/components/TextInput.vue';
  import NumberPad from '@/components/NumberPad.vue';
  import { ref, onMounted } from 'vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import GoBackBtn from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';
  import { MACRO_STEP_TYPES, CURRENCY_CODES } from '@/constants';
  import { useRouter, useRoute } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  import { createMacro, addMacroStep } from '@/apis';

  const router = useRouter();
  const route = useRoute();
  const authStore = useAuthStore();

  const amount = ref('');
  const accountNumber = ref('');
  const bankName = ref('');
  const recipientName = ref('');
  const macroName = ref('');
  const showMacroNameInput = ref(false);
  const showNumberPad = ref(false);
  const showModal = ref(false);
  const showAmountError = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  onMounted(() => {
    // 이전 페이지에서 전달받은 계좌 정보 설정
    accountNumber.value = Array.isArray(route.query.accountNumber)
      ? route.query.accountNumber[0]
      : route.query.accountNumber || '';
    bankName.value = Array.isArray(route.query.bankName)
      ? route.query.bankName[0]
      : route.query.bankName || '국민은행';

    // 받는 사람 이름 더미 데이터 생성
    const dummyNames = [
      '김철수',
      '이영희',
      '박민수',
      '최지영',
      '정수진',
      '한동훈',
      '윤서연',
      '임태호',
      '강미영',
      '조현우',
      '송지은',
      '배준호',
      '오나영',
      '신동욱',
      '홍수빈',
    ];
    const randomIndex = Math.floor(Math.random() * dummyNames.length);
    recipientName.value = dummyNames[randomIndex];
  });

  const handleBack = () => {
    router.push({
      name: 'create-transfer',
      query: {
        accountNumber: accountNumber.value,
        bankName: bankName.value,
        bankCode: route.query.bankCode,
      },
    });
  };

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
    if (!showMacroNameInput.value) {
      macroName.value = `${recipientName.value}으로 ${value}만원 송금`;
    }
  };

  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  const toggleMacroNameInput = () => {
    showMacroNameInput.value = !showMacroNameInput.value;
    if (!showMacroNameInput.value && amount.value) {
      macroName.value = `${recipientName.value}으로 ${amount.value}만원 송금`;
    }
  };

  const handleTransferClick = async () => {
    if (!amount.value || amount.value === '0') {
      showAmountError.value = true;
      setTimeout(() => {
        showAmountError.value = false;
      }, 3000);
      return;
    }

    if (!macroName.value.trim()) {
      macroName.value = `${recipientName.value}으로 ${amount.value}만원 송금`;
    }

    try {
      const macro = await createMacro(authStore.userId, macroName.value.trim());
      const stepData = {
        stepOrder: 1,
        stepType: MACRO_STEP_TYPES.TRANSFER,
        amount: parseInt(amount.value) * 10000,
        currencyCode: CURRENCY_CODES.KRW,
        sourceAccountNo: null,
        sourceBankCode: null,
        targetAccountNo: accountNumber.value,
        targetBankCode: route.query.bankCode || null,
        note: `송금 매크로 - ${amount.value}만원 (${recipientName.value})`,
      };
      await addMacroStep(macro.id, stepData);

      showAlert('매크로 생성 완료', `"${macro.name}" 매크로가 생성되었습니다.`);
      amount.value = '';
      macroName.value = '';
    } catch (error) {
      console.error('매크로 생성 오류:', error);
      showAlert('오류', error.message || '매크로 생성 중 오류가 발생했습니다.');
    }
  };

  const handleModalConfirm = () => {
    showModal.value = false;
    router.push({ name: 'home' });
  };

  const handleModalCancel = () => {
    showModal.value = false;
  };
</script>

<template>
  <main class="w-full h-full flex flex-col justify-between">
    <div class="px-5">
      <div class="pt-10 pb-8">
        <GoBackBtn :handleGoBack="handleBack" />
      </div>
      <h1 class="text-2xl font-bold mb-10">얼마를 송금하시겠어요?</h1>

      <!-- 매크로 이름 설정 -->
      <div class="mb-10">
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm font-medium text-gray-700">매크로 이름</span>
          <button
            @click="toggleMacroNameInput"
            class="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            {{ showMacroNameInput ? '기본값 사용' : '직접 설정' }}
          </button>
        </div>
        <div v-if="showMacroNameInput" class="mb-3">
          <TextInput
            v-model="macroName"
            text=""
            name="macroName"
            :required="true"
            type="text"
            placeholder="예: 월말 송금, 용돈 송금"
          />
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-3">
          <p class="text-sm text-gray-600">
            {{ macroName || '금액을 선택하면 자동으로 설정됩니다' }}
          </p>
        </div>
      </div>

      <!-- 계좌 정보 표시 -->
      <div class="mb-6 p-4 bg-gray-50 rounded-xl">
        <h2 class="text-sm font-medium text-gray-600 mb-2">받는 계좌</h2>
        <p class="text-lg font-semibold text-gray-900">{{ recipientName }}</p>
        <p class="text-sm text-gray-600">{{ bankName }}</p>
        <p class="text-sm text-gray-500">{{ accountNumber }}</p>
      </div>

      <!-- 금액 입력 -->
      <div class="mb-6 pt-3">
        <div @click="handleAmountClick" class="cursor-pointer flex items-center gap-3 pb-3">
          <TextInput
            class="text-xl"
            v-model="amount"
            text="보낼 금액"
            name="amount"
            :required="true"
            type="text"
            placeholder="금액을 입력하세요"
            readonly
          />
          <span class="w-11">만원</span>
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
    </div>

    <div class="px-5 pb-10">
      <!-- 에러 메시지 -->
      <div
        v-if="showAmountError"
        class="mb-3 p-3 bg-red-100 border border-red-300 rounded-xl text-red-700 text-center text-sm"
      >
        금액을 입력해주세요
      </div>

      <PrimaryBtn class="w-full py-2" text="매크로 생성" @click="handleTransferClick" />
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
