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
  });

  const emit = defineEmits(['update:modelValue', 'confirm', 'cancel']);

  const displayValue = ref(props.modelValue.toString());

  watch(
    () => props.modelValue,
    newValue => {
      displayValue.value = newValue.toString();
    }
  );

  const addDigit = digit => {
    if (displayValue.value === '0') {
      displayValue.value = digit.toString();
    } else {
      displayValue.value += digit.toString();
    }
    emit('update:modelValue', displayValue.value);
  };

  const clear = () => {
    displayValue.value = '';
    emit('update:modelValue', '');
  };

  const backspace = () => {
    if (displayValue.value.length > 0) {
      displayValue.value = displayValue.value.slice(0, -1);
      emit('update:modelValue', displayValue.value);
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
