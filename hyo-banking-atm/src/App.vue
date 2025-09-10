<script setup>
  import { RouterView } from 'vue-router';
</script>

<template>
  <div class="flex justify-center items-center min-h-screen bg-white">
    <div class="aspect-[4/3] w-full min-w-[768px]">
      <RouterView />
    </div>
  </div>
</template>
