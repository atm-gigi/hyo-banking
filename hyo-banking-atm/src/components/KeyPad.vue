<script setup>
const keys = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '지움', '0', '정정'];
defineEmits(['keyClick']);
</script>

<template>
  <div class="grid grid-cols-3 gap-4">
    <button
      v-for="key in keys"
      :key="key"
      @click="$emit('keyClick', key)"
      class="w-24 h-20 bg-gray-700 text-white text-2xl font-bold shadow rounded"
    >
      {{ key }}
    </button>
  </div>
</template>
