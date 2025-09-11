<script setup>
  import { ref, watch } from 'vue';

  const props = defineProps({
    text: {
      type: String,
      required: true,
    },
    placeholder: {
      type: String,
      default: '',
    },
    type: {
      type: String,
      default: 'text',
    },
    name: {
      type: String,
      required: true,
    },
    required: {
      type: Boolean,
      default: false,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    modelValue: {
      type: [String, Number],
      default: '',
    },
    formatAccountNumber: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['update:modelValue']);

  const inputValue = ref(props.modelValue);

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

  watch(inputValue, newValue => {
    if (props.formatAccountNumber) {
      // 계좌번호 포맷팅이 필요한 경우, 포맷팅된 값을 그대로 전달
      emit('update:modelValue', newValue);
    } else {
      emit('update:modelValue', newValue);
    }
  });

  watch(
    () => props.modelValue,
    newValue => {
      if (props.formatAccountNumber) {
        // 계좌번호 포맷팅이 필요한 경우, 받은 값이 이미 포맷팅된 값이므로 그대로 사용
        inputValue.value = newValue.toString();
      } else {
        inputValue.value = newValue;
      }
    }
  );
</script>

<template>
  <div class="relative z-0 w-full mb-5 group">
    <input
      :type="type"
      :name="name"
      :id="name"
      v-model="inputValue"
      :readonly="readonly"
      class="block py-2.5 px-0 w-full text-2xl text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-kb-yellow-200 peer"
      placeholder=" "
      :required="required"
    />
    <label
      :for="name"
      class="peer-focus:font-medium absolute text-2xl text-gray-500 duration-300 transform -translate-y-6 scale-75 top-0 -z-10 origin-[0] peer-focus:start-0 rtl:peer-focus:translate-x-1/4 rtl:peer-focus:left-auto peer-focus:text-kb-yellow-200 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-3 peer-focus:scale-75 peer-focus:-translate-y-6"
      >{{ text }}</label
    >
  </div>
</template>
