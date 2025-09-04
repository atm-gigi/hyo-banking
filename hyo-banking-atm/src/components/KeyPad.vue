<script setup>
  const keys = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '지움', '0', '정정'];
  defineEmits(['keyClick']);
</script>

<template>
  <div class="grid grid-cols-3 gap-2">
    <button
      v-for="key in keys"
      :key="key"
      @click="$emit('keyClick', key)"
      class="w-24 h-32 bg-kb-brown-100 text-white text-2xl font-bold shadow rounded-xl active:bg-kb-brown-300 active:scale-105 transition flex items-center justify-center cursor-pointer"
    >
      {{ key }}
    </button>
  </div>
</template>
