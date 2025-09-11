<script setup>
  import { ref, watch } from 'vue';

  const props = defineProps({
    isVisible: {
      type: Boolean,
      default: false,
    },
    modelValue: {
      type: [String, Number],
      default: '',
    },
    currency: {
      type: String,
      default: '만원',
    },
    preserveLeadingZeros: {
      type: Boolean,
      default: false,
    },
    formatAccountNumber: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['update:modelValue', 'confirm', 'cancel']);

  // 계좌번호 포맷팅 함수들
  const formatAccountNumber = value => {
    // 숫자만 추출
    const numbers = value.replace(/\D/g, '');

    // 444444-44-444444 형식으로 포맷팅 (6자리-2자리-6자리)
    if (numbers.length <= 6) {
      return numbers;
    } else if (numbers.length <= 8) {
      return `${numbers.slice(0, 6)}-${numbers.slice(6)}`;
    } else {
      return `${numbers.slice(0, 6)}-${numbers.slice(6, 8)}-${numbers.slice(8, 14)}`;
    }
  };

  const unformatAccountNumber = value => {
    // 하이픈 제거하고 숫자만 반환
    return value.replace(/\D/g, '');
  };

  const displayValue = ref(
    props.formatAccountNumber
      ? formatAccountNumber(props.modelValue.toString())
      : props.modelValue.toString()
  );

  watch(
    () => props.modelValue,
    newValue => {
      if (props.formatAccountNumber) {
        // 받은 값이 이미 포맷팅된 값이므로 그대로 사용
        displayValue.value = newValue.toString();
      } else {
        displayValue.value = newValue.toString();
      }
    }
  );

  const addDigit = digit => {
    let newValue;

    if (props.formatAccountNumber) {
      // 계좌번호 포맷팅이 필요한 경우
      const currentNumbers = unformatAccountNumber(displayValue.value);
      if (currentNumbers.length < 14) {
        // 최대 14자리까지만 입력 가능 (6-2-6)
        newValue = currentNumbers + digit.toString();
        displayValue.value = formatAccountNumber(newValue);
        emit('update:modelValue', displayValue.value); // 포맷팅된 값을 전달
      }
    } else if (props.preserveLeadingZeros) {
      // 계좌번호 등 앞의 0을 보존해야 하는 경우
      displayValue.value += digit.toString();
      emit('update:modelValue', displayValue.value);
    } else {
      // 금액 입력 등 일반적인 경우
      if (displayValue.value === '0') {
        displayValue.value = digit.toString();
      } else {
        displayValue.value += digit.toString();
      }
      emit('update:modelValue', displayValue.value);
    }
  };

  const clear = () => {
    displayValue.value = '';
    emit('update:modelValue', '');
  };

  const backspace = () => {
    if (displayValue.value.length > 0) {
      if (props.formatAccountNumber) {
        // 계좌번호 포맷팅이 필요한 경우
        const currentNumbers = unformatAccountNumber(displayValue.value);
        if (currentNumbers.length > 0) {
          const newValue = currentNumbers.slice(0, -1);
          displayValue.value = formatAccountNumber(newValue);
          emit('update:modelValue', displayValue.value); // 포맷팅된 값을 전달
        }
      } else {
        displayValue.value = displayValue.value.slice(0, -1);
        emit('update:modelValue', displayValue.value);
      }
    }
  };

  const cancel = () => {
    emit('cancel');
  };

  const handleBackdropClick = () => {
    cancel();
  };
</script>

<template>
  <Transition
    enter-active-class="transition-transform duration-300 ease-out"
    enter-from-class="translate-y-full"
    enter-to-class="translate-y-0"
    leave-active-class="transition-transform duration-300 ease-in"
    leave-from-class="translate-y-0"
    leave-to-class="translate-y-full"
  >
    <div
      v-if="isVisible"
      class="fixed inset-0 z-50 flex items-end justify-center"
      @click="handleBackdropClick"
    >
      <!-- Backdrop -->
      <div class="absolute inset-0transition-opacity duration-300"></div>

      <!-- Number Pad -->
      <div v-if="isVisible" class="relative bg-gray-100 rounded-t-3xl w-full max-w-md" @click.stop>
        <!-- Handle bar -->
        <div class="flex justify-center pt-3 pb-2">
          <div class="w-12 h-1 bg-gray-300 rounded-full"></div>
        </div>

        <!-- Display -->
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="text-right">
            <div class="text-3xl font-bold text-gray-900">
              {{ displayValue || '0' }}
            </div>
            <div class="text-sm text-gray-500 mt-1">
              {{ currency }}
            </div>
          </div>
        </div>

        <!-- Number Pad Grid -->
        <div class="p-6">
          <div class="grid grid-cols-3 gap-4">
            <!-- Numbers 1-9 -->
            <button
              v-for="num in [1, 2, 3, 4, 5, 6, 7, 8, 9]"
              :key="num"
              @click="addDigit(num)"
              class="h-16 bg-gray-200 hover:bg-gray-300 active:bg-gray-300 rounded-xl font-semibold text-xl transition-colors duration-150"
            >
              {{ num }}
            </button>

            <!-- Bottom row -->
            <button
              @click="clear"
              class="h-16 bg-gray-200 hover:bg-gray-300 active:bg-gray-300 rounded-xl font-semibold text-lg transition-colors duration-150"
            >
              C
            </button>
            <button
              @click="addDigit(0)"
              class="h-16 bg-gray-200 hover:bg-gray-300 active:bg-gray-300 rounded-xl font-semibold text-xl transition-colors duration-150"
            >
              0
            </button>
            <button
              @click="backspace"
              class="h-16 bg-gray-200 hover:bg-gray-300 active:bg-gray-300 rounded-xl font-semibold text-lg transition-colors duration-150"
            >
              ⌫
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>
