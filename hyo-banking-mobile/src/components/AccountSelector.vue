<script setup>
  import { defineProps, defineEmits, watch } from 'vue';
  import { getBankNameByCode } from '@/constants';

  const props = defineProps({
    accounts: {
      type: Array,
      default: () => [],
    },
    selectedAccount: {
      type: Object,
      default: null,
    },
    isLoading: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: '계좌를 선택하세요',
    },
    radioName: {
      type: String,
      default: 'account-selection',
    },
  });

  const emit = defineEmits(['select']);

  // selectedAccount 값 변화 감지
  watch(
    () => props.selectedAccount,
    () => {
      console.log(' selected account ⚠️  : ', props.selectedAccount);
      console.log(' accounts ⚠️  : ', props.accounts[1]);
    }
  );

  const selectAccount = account => {
    emit('select', account);
  };
</script>

<template>
  <div>
    <!-- 선택된 계좌 요약 -->
    <div v-if="props.selectedAccount" class="mb-6 p-4 border border-kb-yellow-100 rounded-lg">
      <div class="flex justify-between items-center">
        <div>
          <div class="font-semibold">
            {{
              getBankNameByCode(props.selectedAccount.bankCode) || props.selectedAccount.bankName
            }}
          </div>
          <div class="text-md font-bold">계좌번호 {{ props.selectedAccount.accountNo }}</div>
        </div>
        <div class="text-right">
          <div class="font-semibold">{{ props.selectedAccount.balance?.toLocaleString() }}원</div>
        </div>
      </div>
    </div>

    <!-- 계좌 선택 -->
    <div class="mb-6">
      <label class="block text-sm font-medium text-gray-700 mb-2">
        {{ props.placeholder }}
      </label>
      <div v-if="props.isLoading" class="text-center py-4 text-gray-500">
        계좌 목록을 불러오는 중...
      </div>
      <div v-else-if="props.accounts.length === 0" class="text-center py-4 text-gray-500">
        등록된 계좌가 없습니다.
      </div>
      <div v-else class="space-y-2">
        <label
          v-for="account in props.accounts"
          :key="account.id"
          :class="[
            'block w-full p-3 rounded-lg border-2 transition-colors cursor-pointer relative',
            props.selectedAccount && props.selectedAccount.accountId === account.accountId
              ? 'border-kb-yellow-100 bg-white'
              : 'border-gray-200 hover:border-gray-300',
          ]"
          @click="selectAccount(account)"
        >
          <input
            type="radio"
            :name="props.radioName"
            :value="account.id"
            :checked="
              props.selectedAccount && props.selectedAccount.accountId === account.accountId
            "
            @change="selectAccount(account)"
            class="sr-only"
          />

          <!-- 라디오 버튼 스타일의 선택 표시 -->
          <div
            class="absolute top-3 left-3 w-5 h-5 border-2 rounded-full flex items-center justify-center"
            :class="
              props.selectedAccount && props.selectedAccount.accountId === account.accountId
                ? 'border-kb-yellow-100 bg-white'
                : 'border-gray-300'
            "
          >
            <div
              v-if="props.selectedAccount && props.selectedAccount.id === account.accountId"
              class="w-2 h-2 bg-white rounded-full"
            ></div>
          </div>

          <div class="flex justify-between items-center pl-8">
            <div>
              <div class="font-medium">
                {{ getBankNameByCode(account.bankCode) || account.bankName }}
              </div>
              <div class="text-sm text-gray-600">{{ account.accountNo }}</div>
            </div>
            <div class="text-right">
              <div class="font-medium">{{ account.balance?.toLocaleString() }}원</div>
            </div>
          </div>
        </label>
      </div>
    </div>
  </div>
</template>
