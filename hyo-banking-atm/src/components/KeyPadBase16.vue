<script setup>
  const keys = [
    '1',
    '2',
    '3',
    'a',
    '4',
    '5',
    '6',
    'b',
    '7',
    '8',
    '9',
    'c',
    '0',
    'd',
    'e',
    'f',
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
      @mousedown.prevent
      @touchstart.prevent
      @click="$emit('keyClick', key)"
      class="w-24 h-32 bg-kb-brown-100 text-white text-2xl font-bold shadow rounded-xl active:bg-kb-brown-300 active:scale-105 transition flex items-center justify-center cursor-pointer"
    >
      {{ key }}
    </button>
  </div>
</template>
