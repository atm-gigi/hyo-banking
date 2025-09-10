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
  });

  const emit = defineEmits(['update:modelValue']);

  const inputValue = ref(props.modelValue);

  watch(inputValue, newValue => {
    emit('update:modelValue', newValue);
  });

  watch(
    () => props.modelValue,
    newValue => {
      inputValue.value = newValue;
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
