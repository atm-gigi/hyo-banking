<template>
  <div class="flex-1 flex flex-col">
    <div class="flex-1">
      <!-- 금액 입력 -->
      <div @click="handleAmountClick" class="cursor-pointer flex items-center gap-3 pb-3 mb-6">
        <TextInput
          class="text-xl"
          v-model="formData.amount"
          text="금액"
          name="amount"
          :required="true"
          type="text"
          placeholder="금액을 입력하세요"
          readonly
        />
        <span class="w-11 font-bold text-lg">만원</span>
      </div>

      <!-- 금액 선택 버튼들 -->
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

      <div class="flex gap-3">
        <button
          @click="handleBack"
          class="flex-1 h-14 bg-gray-200 hover:bg-gray-300 active:bg-gray-400 rounded-xl font-semibold text-lg transition-colors duration-150"
        >
          이전
        </button>
        <PrimaryBtn class="flex-1 py-2" text="다음" @click="handleNext" />
      </div>
    </div>

    <!-- NumberPad for Amount -->
    <NumberPad
      v-if="activeInputField === 'amount'"
      v-model="formData.amount"
      :isVisible="showNumberPad"
      currency="만원"
      @confirm="handleNumberPadConfirm"
      @cancel="handleNumberPadCancel"
    />
  </div>
</template>

<script setup>
  import TextInput from '@/components/TextInput.vue';
  import NumberPad from '@/components/NumberPad.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import { ref, watch } from 'vue';

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

  const emit = defineEmits(['next', 'back', 'update:formData']);

  const showNumberPad = ref(false);
  const showAmountError = ref(false);
  const activeInputField = ref('');

  // 금액이 변경될 때마다 매크로 이름 자동 업데이트
  watch(
    () => props.formData.amount,
    newAmount => {
      if (newAmount && !props.formData.showMacroNameInput) {
        emit('update:formData', {
          macroName: `${newAmount}만원 출금`,
        });
      }
    }
  );

  const handleAmountClick = () => {
    activeInputField.value = 'amount';
    showNumberPad.value = true;
  };

  const handleNumberPadConfirm = () => {
    showNumberPad.value = false;
    activeInputField.value = '';
  };

  const handleNumberPadCancel = () => {
    showNumberPad.value = false;
    activeInputField.value = '';
  };

  const handleAmountSelect = value => {
    emit('update:formData', {
      amount: value,
    });
    if (!props.formData.showMacroNameInput) {
      emit('update:formData', {
        macroName: `${value}만원 출금`,
      });
    }
  };

  const handleNext = () => {
    if (!props.formData.amount || props.formData.amount === '0') {
      showAmountError.value = true;
      setTimeout(() => {
        showAmountError.value = false;
      }, 3000);
      return;
    }

    emit('next');
  };

  const handleBack = () => {
    emit('back');
  };
</script>
