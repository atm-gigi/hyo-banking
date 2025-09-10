<script setup>
  import TextInput from '@/components/TextInput.vue';
  import NumberPad from '@/components/NumberPad.vue';
  import BankSelector from '@/components/BankSelector.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import { ref } from 'vue';
  import { getUserByAccount } from '@/apis';

  const props = defineProps({
    formData: {
      type: Object,
      required: true,
    },
    isLoading: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['next', 'update:formData', 'update:foundUser']);

  const showNumberPad = ref(false);
  const showBankSelector = ref(false);
  const showAccountError = ref(false);
  const isValidating = ref(false);
  const validationMessage = ref('');
  const isValidUser = ref(false);
  const foundUser = ref(null);
  const activeInputField = ref('');

  // 사용자 조회 함수
  const checkUserByAccount = async () => {
    if (!props.formData.accountNumber.trim() || !props.formData.selectedBank?.code) {
      validationMessage.value = '';
      isValidUser.value = false;
      return;
    }

    isValidating.value = true;
    validationMessage.value = '';

    try {
      const user = await getUserByAccount(
        props.formData.accountNumber,
        props.formData.selectedBank.code
      );
      foundUser.value = user;
      isValidUser.value = true;
      validationMessage.value = `✅ ${user.name}님의 계좌가 확인되었습니다.`;
      emit('update:foundUser', user);
    } catch (error) {
      console.error('사용자 조회 오류:', error);
      foundUser.value = null;
      isValidUser.value = false;
      validationMessage.value =
        '❌ 해당 계좌의 사용자를 찾을 수 없습니다. 계좌번호와 은행을 다시 확인해주세요.';
      emit('update:foundUser', null);
    } finally {
      isValidating.value = false;
    }
  };

  const handleAccountNumberClick = () => {
    activeInputField.value = 'accountNumber';
    showNumberPad.value = true;
  };

  const handleNumberPadConfirm = () => {
    showNumberPad.value = false;
    activeInputField.value = '';
    // 계좌번호가 변경되면 사용자 조회
    checkUserByAccount();
  };

  const handleNumberPadCancel = () => {
    showNumberPad.value = false;
    activeInputField.value = '';
  };

  const handleBankClick = () => {
    showBankSelector.value = true;
  };

  const handleBankConfirm = bank => {
    emit('update:formData', {
      selectedBank: bank,
      bankName: bank.name,
    });
    showBankSelector.value = false;
    // 은행이 변경되면 사용자 조회
    checkUserByAccount();
  };

  const handleBankCancel = () => {
    showBankSelector.value = false;
  };

  const handleNext = () => {
    if (!props.formData.accountNumber.trim()) {
      showAccountError.value = true;
      setTimeout(() => {
        showAccountError.value = false;
      }, 3000);
      return;
    }

    if (!isValidUser.value) {
      showAccountError.value = true;
      setTimeout(() => {
        showAccountError.value = false;
      }, 3000);
      return;
    }

    emit('next');
  };
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex-1">
      <h2 class="text-xl font-bold mb-6">누구에게 돈을 보내나요?</h2>

      <!-- 계좌번호 입력 -->
      <div class="mb-6">
        <div @click="handleAccountNumberClick" class="cursor-pointer flex items-center gap-3 pb-3">
          <TextInput
            :model-value="formData.accountNumber"
            @update:model-value="value => emit('update:formData', { accountNumber: value })"
            text="계좌번호"
            name="accountNumber"
            :required="true"
            type="text"
            placeholder="계좌번호를 입력하세요"
            readonly
          />
        </div>
      </div>

      <!-- 은행 선택 -->
      <div class="mb-6">
        <div @click="handleBankClick" class="cursor-pointer flex items-center gap-3 pb-3">
          <TextInput
            :model-value="formData.bankName"
            @update:model-value="value => emit('update:formData', { bankName: value })"
            text="은행명"
            name="bankName"
            :required="false"
            type="text"
            placeholder="은행을 선택하세요"
            readonly
          />
        </div>
      </div>

      <!-- 사용자 조회 메시지 -->
      <div v-if="validationMessage" class="mb-6">
        <div
          :class="[
            'p-3 rounded-lg text-sm text-center',
            isValidUser
              ? 'bg-green-100 text-green-700 border border-green-300'
              : 'bg-red-100 text-red-700 border border-red-300',
          ]"
        >
          {{ validationMessage }}
        </div>
      </div>

      <!-- 조회 중 메시지 -->
      <div v-if="isValidating" class="mb-6">
        <div
          class="p-3 rounded-lg text-sm text-center bg-blue-100 text-blue-700 border border-blue-300"
        >
          🔍 사용자를 확인하는 중...
        </div>
      </div>
    </div>

    <div class="px-5 pb-10">
      <!-- 에러 메시지 -->
      <div
        v-if="showAccountError"
        class="mb-3 p-3 bg-red-100 border border-red-300 rounded-xl text-red-700 text-center text-sm"
      >
        {{
          !formData.accountNumber.trim()
            ? '계좌번호를 입력해주세요'
            : '등록된 사용자의 계좌를 선택해주세요'
        }}
      </div>

      <PrimaryBtn class="w-full py-2" text="다음" @click="handleNext" />
    </div>

    <!-- NumberPad for Account Number -->
    <NumberPad
      v-if="activeInputField === 'accountNumber'"
      :model-value="formData.accountNumber"
      @update:model-value="value => emit('update:formData', { accountNumber: value })"
      :isVisible="showNumberPad"
      currency=""
      @confirm="handleNumberPadConfirm"
      @cancel="handleNumberPadCancel"
    />

    <!-- BankSelector -->
    <BankSelector
      :isVisible="showBankSelector"
      :selectedBank="formData.selectedBank"
      @confirm="handleBankConfirm"
      @cancel="handleBankCancel"
    />
  </div>
</template>
