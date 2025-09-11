<script setup>
  import TextInput from '@/components/TextInput.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import { getBankNameByCode } from '@/constants';

  const props = defineProps({
    formData: {
      type: Object,
      required: true,
    },
    foundUser: {
      type: Object,
      default: null,
    },
    isLoading: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['create', 'back', 'update:formData']);

  const toggleMacroNameInput = () => {
    emit('update:formData', {
      showMacroNameInput: !props.formData.showMacroNameInput,
    });

    if (!props.formData.showMacroNameInput && props.formData.amount) {
      emit('update:formData', {
        macroName: `${props.formData.amount}만원 송금`,
      });
    }
  };

  const handleCreate = () => {
    if (!props.formData.macroName.trim()) {
      emit('update:formData', {
        macroName: `${props.formData.amount}만원 송금`,
      });
    }
    emit('create');
  };

  const handleBack = () => {
    emit('back');
  };
</script>

<template>
  <div class="flex-1 flex flex-col">
    <div class="flex-1">
      <!-- 매크로 이름 설정 -->
      <div class="mb-10">
        <div class="flex items-center justify-between mb-3">
          <span class="text-sm font-medium text-gray-700">간편 거래 이름</span>
          <button
            @click="toggleMacroNameInput"
            class="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            {{ formData.showMacroNameInput ? '기본값 사용' : '직접 설정' }}
          </button>
        </div>
        <div v-if="formData.showMacroNameInput" class="mb-3">
          <TextInput
            :model-value="formData.macroName"
            @update:model-value="value => emit('update:formData', { macroName: value })"
            text=""
            name="macroName"
            :required="true"
            type="text"
            placeholder="예: 월말 송금, 급여 송금"
          />
        </div>
        <div v-else class="bg-gray-50 rounded-lg p-3">
          <p class="text-sm text-gray-600">
            {{ formData.macroName || '금액을 선택하면 자동으로 설정됩니다' }}
          </p>
        </div>
      </div>

      <!-- 송금 정보 요약 -->
      <div class="bg-gray-50 rounded-lg p-4 mb-6">
        <h3 class="text-lg font-semibold mb-3">송금 정보 요약</h3>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-600">받는 사람:</span>
            <span class="font-medium">{{ foundUser?.name || '확인 중...' }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">계좌번호:</span>
            <span class="font-medium">{{ formData.accountNumber }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">은행:</span>
            <span class="font-medium">{{
              getBankNameByCode(formData.selectedBank?.code) || formData.bankName
            }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">송금 금액:</span>
            <span class="font-medium">{{ formData.amount }}만원</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">간편 거래 이름:</span>
            <span class="font-medium">{{ formData.macroName }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="px-5 pb-10">
      <div class="flex gap-3">
        <button
          @click="handleBack"
          class="flex-1 h-14 bg-gray-200 hover:bg-gray-300 active:bg-gray-400 rounded-xl font-semibold text-lg transition-colors duration-150"
        >
          이전
        </button>
        <PrimaryBtn
          class="flex-1 py-2"
          :text="isLoading ? '생성 중...' : '간편 거래 생성'"
          :disabled="isLoading"
          @click="handleCreate"
        />
      </div>
    </div>
  </div>
</template>
