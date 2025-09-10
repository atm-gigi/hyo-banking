<template>
  <button
    :disabled="disabled"
    @click="handleClick"
    class="bg-kb-brown-100
    shadow-md hover:shadow-lg
    active:bg-kb-brown-300 active:scale-95 active:shadow-inner',
    text-white font-bold rounded-xl text-5xl transition py-8
    duration-300 flex items-center justify-center cursor-pointer
    disabled:opacity-50 disabled:active:scale-100 disabled:cursor-none"
  >
    {{ text }}
  </button>
</template>

<script setup>
  const props = defineProps({
    text: {
      type: String,
      required: true,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['click']);

  const handleClick = () => {
    if (!props.disabled) {
      emit('click');
    }
  };
</script>
