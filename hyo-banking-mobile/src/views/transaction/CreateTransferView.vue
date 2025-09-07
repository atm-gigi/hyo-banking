<script setup lang="ts">
  import TextInput from '@/components/TextInput.vue';
  import NumberPad from '@/components/NumberPad.vue';
  import BankSelector from '@/components/BankSelector.vue';
  import { ref, onMounted } from 'vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import { useRouter, useRoute } from 'vue-router';

  const router = useRouter();
  const route = useRoute();
  const accountNumber = ref('');
  const bankName = ref('');
  const selectedBank = ref(null);
  const showAccountError = ref(false);
  const showNumberPad = ref(false);
  const showBankSelector = ref(false);

  onMounted(() => {
    // 쿼리 파라미터에서 계좌 정보 초기화
    if (route.query.accountNumber) {
      accountNumber.value = Array.isArray(route.query.accountNumber)
        ? route.query.accountNumber[0]
        : route.query.accountNumber;
    }
    if (route.query.bankName) {
      bankName.value = Array.isArray(route.query.bankName)
        ? route.query.bankName[0]
        : route.query.bankName;
    }
    if (route.query.bankCode) {
      // 은행 코드로 selectedBank 설정
      const bankCode = Array.isArray(route.query.bankCode)
        ? route.query.bankCode[0]
        : route.query.bankCode;
      selectedBank.value = { code: bankCode, name: bankName.value };
    }
  });

  const handleBackClick = () => {
    router.push({ name: 'home' });
  };

  const handleAccountNumberClick = () => {
    showNumberPad.value = true;
  };

  const handleNumberPadConfirm = value => {
    accountNumber.value = value;
    showNumberPad.value = false;
  };

  const handleNumberPadCancel = () => {
    showNumberPad.value = false;
  };

  const handleBankClick = () => {
    showBankSelector.value = true;
  };

  const handleBankConfirm = bank => {
    selectedBank.value = bank;
    bankName.value = bank.name;
    showBankSelector.value = false;
  };

  const handleBankCancel = () => {
    showBankSelector.value = false;
  };

  const handleNext = () => {
    if (!accountNumber.value.trim()) {
      showAccountError.value = true;
      setTimeout(() => {
        showAccountError.value = false;
      }, 3000);
      return;
    }

    // 계좌 정보를 다음 페이지로 전달
    router.push({
      name: 'transfer-amount',
      query: {
        accountNumber: accountNumber.value,
        bankName: bankName.value || '국민은행',
        bankCode: selectedBank.value?.code || '001',
      },
    });
  };
</script>

<template>
  <main class="w-full h-full flex flex-col justify-between">
    <div class="px-5">
      <div class="pt-10">
        <button
          @click="handleBackClick"
          class="flex items-center hover:bg-gray-100 rounded-full transition-colors py-2 px-3"
        >
          <div class="mr-4">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 19l-7-7 7-7"
              ></path>
            </svg>
          </div>
          <span>뒤로가기</span>
        </button>
      </div>

      <h1 class="text-2xl font-bold pt-10 mb-10">누구에게 돈을 보내나요?</h1>

      <!-- 계좌번호 입력 -->
      <div class="mb-6">
        <div @click="handleAccountNumberClick" class="cursor-pointer flex items-center gap-3 pb-3">
          <TextInput
            v-model="accountNumber"
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
            v-model="bankName"
            text="은행명"
            name="bankName"
            :required="false"
            type="text"
            placeholder="은행을 선택하세요"
            readonly
          />
        </div>
      </div>
    </div>

    <div class="px-5 pb-10">
      <!-- 에러 메시지 -->
      <div
        v-if="showAccountError"
        class="mb-3 p-3 bg-red-100 border border-red-300 rounded-xl text-red-700 text-center text-sm"
      >
        계좌번호를 입력해주세요
      </div>

      <PrimaryBtn class="w-full py-2" text="다음" @click="handleNext" />
    </div>

    <!-- NumberPad -->
    <NumberPad
      v-model="accountNumber"
      :isVisible="showNumberPad"
      currency=""
      @confirm="handleNumberPadConfirm"
      @cancel="handleNumberPadCancel"
    />

    <!-- BankSelector -->
    <BankSelector
      :isVisible="showBankSelector"
      :selectedBank="selectedBank"
      @confirm="handleBankConfirm"
      @cancel="handleBankCancel"
    />
  </main>
</template>
