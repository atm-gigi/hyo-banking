<template>
  <button @click="handleClick" class="cursor-pointer active:brightness-50 transition w-full">
    <div class="flex flex-row justify-between items-center">
      <img :src="iconSrc" :alt="iconAlt" class="size-9" />
      <span class="font-bold pl-5 text-start w-full">{{ text }}</span>
      <img class="size-4" width="30" height="30" :src="ICON_URLS.FORWARD" alt="forward" />
    </div>
  </button>
</template>

<script setup>
  import { ICON_URLS } from '@/constants';

  defineProps({
    text: {
      type: String,
      required: true,
    },
    iconSrc: {
      type: String,
      required: true,
    },
    iconAlt: {
      type: String,
      required: true,
    },
  });

  const emit = defineEmits(['click']);

  const handleClick = () => {
    emit('click');
  };
</script>
