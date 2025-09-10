<script setup lang="ts">
  import { ref, reactive } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import TransferStep1 from '@/components/transfer/TransferStep1.vue';
  import TransferStep2 from '@/components/transfer/TransferStep2.vue';
  import TransferStep3 from '@/components/transfer/TransferStep3.vue';
  import Modal from '@/components/Modal.vue';
  import { createMacro, addMacroStep } from '@/apis';

  const router = useRouter();
  const route = useRoute();
  const authStore = useAuthStore();

  // 반응형 데이터
  const isLoading = ref(false);
  const currentStep = ref(1); // 1: 계좌선택, 2: 금액, 3: 매크로이름
  const transferData = reactive({
    accountNumber: '',
    bankName: '',
    selectedBank: null,
    amount: '',
    macroName: '',
    showMacroNameInput: false,
  });
  const foundUser = ref(null);
  const transferMessage = ref(null);
  const showModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  // 쿼리 파라미터에서 계좌 정보 초기화
  const initializeFromQuery = () => {
    if (route.query.accountNumber) {
      transferData.accountNumber = Array.isArray(route.query.accountNumber)
        ? route.query.accountNumber[0]
        : route.query.accountNumber;
    }
    if (route.query.bankName) {
      transferData.bankName = Array.isArray(route.query.bankName)
        ? route.query.bankName[0]
        : route.query.bankName;
    }
    if (route.query.bankCode) {
      const bankCode = Array.isArray(route.query.bankCode)
        ? route.query.bankCode[0]
        : route.query.bankCode;
      transferData.selectedBank = { code: bankCode, name: transferData.bankName };
    }
  };

  // 3단계 처리 (매크로 생성)
  const handleStep3 = async () => {
    isLoading.value = true;
    transferMessage.value = null;

    try {
      // 매크로 생성
      const macro = await createMacro(authStore.userId, transferData.macroName.trim());
      const stepData = {
        stepOrder: 1,
        stepType: 'TRANSFER',
        amount: parseInt(transferData.amount) * 10000,
        currencyCode: 'KRW',
        sourceAccountNo: null, // 송금자의 계좌는 매크로 실행 시 선택
        sourceBankCode: null,
        targetAccountNo: transferData.accountNumber,
        targetBankCode: transferData.selectedBank.code,
        note: `송금 매크로 - ${transferData.amount}만원 (${foundUser.value?.name || '알 수 없음'}님에게)`,
      };
      await addMacroStep(macro.id, stepData);

      showAlert('매크로 생성 완료', `"${macro.name}" 매크로가 생성되었습니다.`);
    } catch (error) {
      console.error('매크로 생성 오류:', error);
      transferMessage.value = {
        type: 'error',
        text: error.message || '매크로 생성 중 오류가 발생했습니다.',
      };
    } finally {
      isLoading.value = false;
    }
  };

  // 다음 단계로 이동
  const handleNext = () => {
    currentStep.value++;
  };

  // 이전 단계로 이동
  const handleBack = () => {
    currentStep.value--;
    transferMessage.value = null;
  };

  // 폼 데이터 업데이트
  const updateFormData = newData => {
    Object.assign(transferData, newData);
  };

  // 찾은 사용자 업데이트
  const updateFoundUser = user => {
    foundUser.value = user;
  };

  // 모달 표시
  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  // 모달 확인
  const handleModalConfirm = () => {
    showModal.value = false;
    router.push({ name: 'home' });
  };

  // 모달 취소
  const handleModalCancel = () => {
    showModal.value = false;
  };

  // 뒤로가기 핸들러
  const handleGoBack = () => {
    if (currentStep.value > 1) {
      handleBack();
    } else {
      router.push({ name: 'home' });
    }
  };

  // 초기화
  initializeFromQuery();
</script>

<template>
  <main class="w-full h-full flex flex-col justify-between">
    <div class="px-5 h-full flex flex-col">
      <div class="pt-10 pb-8">
        <BackButton :handle-go-back="handleGoBack" />
      </div>

      <h1 class="text-2xl font-bold mb-10">
        {{
          currentStep === 1
            ? '누구에게 돈을 보내나요? (1/3)'
            : currentStep === 2
              ? '얼마를 보내시겠어요? (2/3)'
              : '매크로 이름을 설정하세요 (3/3)'
        }}
      </h1>

      <!-- 1단계: 계좌 선택 -->
      <TransferStep1
        v-if="currentStep === 1"
        :form-data="transferData"
        :is-loading="isLoading"
        @next="handleNext"
        @update:form-data="updateFormData"
        @update:found-user="updateFoundUser"
        class="flex-1"
      />

      <!-- 2단계: 금액 입력 -->
      <TransferStep2
        v-if="currentStep === 2"
        :form-data="transferData"
        :is-loading="isLoading"
        @next="handleNext"
        @back="handleBack"
        @update:form-data="updateFormData"
        class="flex-1"
      />

      <!-- 3단계: 매크로 이름 설정 및 생성 -->
      <TransferStep3
        v-if="currentStep === 3"
        :form-data="transferData"
        :found-user="foundUser"
        :is-loading="isLoading"
        @create="handleStep3"
        @back="handleBack"
        @update:form-data="updateFormData"
        class="flex-1"
      />
    </div>

    <!-- 메시지 표시 -->
    <div v-if="transferMessage" class="px-5">
      <div
        :class="[
          'text-center text-sm p-3 rounded-lg whitespace-pre-line',
          transferMessage.type === 'error'
            ? 'bg-red-100 text-red-700'
            : 'bg-green-100 text-green-700',
        ]"
      >
        {{ transferMessage.text }}
      </div>
    </div>

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
