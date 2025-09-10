<script setup>
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import AccountSelector from '@/components/AccountSelector.vue';
  import { ref, onMounted } from 'vue';
  import { getAccounts } from '@/apis';

  const props = defineProps({
    formData: {
      type: Object,
      required: true,
    },
    isLoading: {
      type: Boolean,
      default: false,
    },
    userId: {
      type: Number,
      required: true,
    },
  });

  const emit = defineEmits(['next', 'update:formData']);

  const showAccountError = ref(false);
  const userAccounts = ref([]);
  const isLoadingAccounts = ref(false);

  // 계좌 목록 로드
  const loadUserAccounts = async () => {
    isLoadingAccounts.value = true;
    try {
      const accounts = await getAccounts(props.userId);
      userAccounts.value = accounts;
    } catch (error) {
      console.error('계좌 목록 로드 오류:', error);
      userAccounts.value = [];
    } finally {
      isLoadingAccounts.value = false;
    }
  };

  // 계좌 선택
  const handleAccountSelect = account => {
    emit('update:formData', {
      selectedAccount: account,
      sourceAccountNo: account.accountNo,
      sourceBankCode: account.bankCode,
    });
  };

  // 컴포넌트 마운트 시
  onMounted(() => {
    loadUserAccounts();
  });

  const handleNext = () => {
    if (!props.formData.selectedAccount) {
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
      <AccountSelector
        :accounts="userAccounts"
        :selectedAccount="formData.selectedAccount"
        :isLoading="isLoadingAccounts"
        placeholder="출금할 계좌를 선택하세요"
        @select="handleAccountSelect"
      />
    </div>

    <div class="px-5 pb-10">
      <!-- 에러 메시지 -->
      <div
        v-if="showAccountError"
        class="mb-3 p-3 bg-red-100 border border-red-300 rounded-xl text-red-700 text-center text-sm"
      >
        계좌를 선택해주세요
      </div>

      <PrimaryBtn class="w-full py-2" text="다음" @click="handleNext" />
    </div>
  </div>
</template>
