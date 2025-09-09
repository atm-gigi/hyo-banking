<script setup>
  const keys = [
    '1',
    '2',
    '3',
    'A',
    '4',
    '5',
    '6',
    'B',
    '7',
    '8',
    '9',
    'C',
    '0',
    'D',
    'E',
    'F',
    '결정',
    '지움',
    '정정',
    '',
  ];
  defineEmits(['keyClick']);
</script>

<template>
  <div class="grid grid-cols-4 gap-2">
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
