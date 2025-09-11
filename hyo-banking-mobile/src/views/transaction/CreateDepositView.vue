<script setup lang="ts">
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import DepositStep1 from '@/components/deposit/DepositStep1.vue';
  import DepositStep2 from '@/components/deposit/DepositStep2.vue';
  import DepositStep3 from '@/components/deposit/DepositStep3.vue';
  import Modal from '@/components/Modal.vue';
  import { createMacro, addMacroStep } from '@/apis';

  const router = useRouter();
  const authStore = useAuthStore();

  // 반응형 데이터
  const isLoading = ref(false);
  const currentStep = ref(1); // 1: 계좌정보, 2: 금액, 3: 매크로이름
  const depositData = reactive({
    targetAccount: '',
    bankName: '',
    selectedBank: null,
    selectedAccount: null,
    amount: '',
    macroName: '',
    showMacroNameInput: false,
  });
  const depositMessage = ref(null);
  const showModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  // 3단계 처리 (매크로 생성)
  const handleStep3 = async () => {
    isLoading.value = true;
    depositMessage.value = null;

    try {
      // 매크로 생성
      const macro = await createMacro(authStore.userId, depositData.macroName.trim());
      const stepData = {
        stepOrder: 1,
        stepType: 'DEPOSIT',
        amount: parseInt(depositData.amount) * 10000,
        currencyCode: 'KRW',
        sourceAccountNo: null,
        sourceBankCode: null,
        targetAccountNo: depositData.targetAccount,
        targetBankCode: depositData.selectedBank.code,
        note: `입금 - ${depositData.amount}만원`,
      };
      await addMacroStep(macro.id, stepData);

      showAlert('간편거래 생성 완료', `"${macro.name}" 간편 거래가 생성되었습니다.`);
    } catch (error) {
      console.error('간편 거래 생성 오류:', error);
      depositMessage.value = {
        type: 'error',
        text: error.message || '간편 거래 생성 중 오류가 발생했습니다.',
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
    depositMessage.value = null;
  };

  // 폼 데이터 업데이트
  const updateFormData = newData => {
    Object.assign(depositData, newData);
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
            ? '어디로 입금하시겠어요? (1/3)'
            : currentStep === 2
              ? '얼마를 넣으시겠어요? (2/3)'
              : '간편 거래 이름을 설정하세요 (3/3)'
        }}
      </h1>

      <!-- 1단계: 계좌 정보 입력 -->
      <DepositStep1
        v-if="currentStep === 1"
        :form-data="depositData"
        :is-loading="isLoading"
        :user-id="authStore.userId"
        @next="handleNext"
        @update:form-data="updateFormData"
        class="flex-1"
      />

      <!-- 2단계: 금액 입력 -->
      <DepositStep2
        v-if="currentStep === 2"
        :form-data="depositData"
        :is-loading="isLoading"
        @next="handleNext"
        @back="handleBack"
        @update:form-data="updateFormData"
        class="flex-1"
      />

      <!-- 3단계: 매크로 이름 설정 및 생성 -->
      <DepositStep3
        v-if="currentStep === 3"
        :form-data="depositData"
        :is-loading="isLoading"
        @create="handleStep3"
        @back="handleBack"
        @update:form-data="updateFormData"
        class="flex-1"
      />
    </div>

    <!-- 메시지 표시 -->
    <div v-if="depositMessage" class="px-5">
      <div
        :class="[
          'text-center text-sm p-3 rounded-lg whitespace-pre-line',
          depositMessage.type === 'error'
            ? 'bg-red-100 text-red-700'
            : 'bg-green-100 text-green-700',
        ]"
      >
        {{ depositMessage.text }}
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
