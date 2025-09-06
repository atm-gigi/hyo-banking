<template>
  <button
    :disabled="disabled"
    @click="handleClick"
    class="bg-kb-brown-100 rounded-2xl py-6 text-white font-bold text-center hover:scale-105 active:scale-95 active:brightness-75 transition duration-300 shadow-sm cursor-pointer"
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
