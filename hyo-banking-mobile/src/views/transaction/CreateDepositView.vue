<script setup lang="ts">
  import TextInput from '@/components/TextInput.vue';
  import NumberPad from '@/components/NumberPad.vue';
  import { ref } from 'vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  import { createMacro, addMacroStep } from '@/apis';

  const router = useRouter();
  const authStore = useAuthStore();
  const amount = ref('');
  const showNumberPad = ref(false);
  const showModal = ref(false);
  const showAmountError = ref(false);
  const macroName = ref('');
  const showMacroNameInput = ref(false);
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
    if (!showMacroNameInput.value) {
      macroName.value = `${value}만원 입금`;
    }
  };

  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  const toggleMacroNameInput = () => {
    showMacroNameInput.value = !showMacroNameInput.value;
    if (!showMacroNameInput.value && amount.value) {
      macroName.value = `${amount.value}만원 입금`;
    }
  };

  const handleAddTransaction = async () => {
    if (!amount.value || amount.value === '0') {
      showAmountError.value = true;
      setTimeout(() => {
        showAmountError.value = false;
      }, 3000);
      return;
    }

    if (!macroName.value.trim()) {
      macroName.value = `${amount.value}만원 입금`;
    }

    try {
      const macro = await createMacro(authStore.userId, macroName.value.trim());
      const stepData = {
        stepOrder: 1,
        stepType: 'DEPOSIT',
        amount: parseInt(amount.value) * 10000,
        currencyCode: 'KRW',
        sourceAccountNo: null,
        sourceBankCode: null,
        targetAccountNo: null,
        targetBankCode: null,
        note: `입금 매크로 - ${amount.value}만원`,
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
        <BackButton />
      </div>

      <h1 class="text-2xl font-bold mb-10">얼마를 넣으시겠어요?</h1>

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
            placeholder="예: 월말 입금, 급여 입금"
          />
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-3">
          <p class="text-sm text-gray-600">
            {{ macroName || '금액을 선택하면 자동으로 설정됩니다' }}
          </p>
        </div>
      </div>

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

      <PrimaryBtn class="w-full py-2" text="매크로 생성" @click="handleAddTransaction" />
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
