<script setup>
  import { RouterView } from 'vue-router';
</script>

<template>
  <div class="flex justify-center items-center min-h-screen bg-gray-100">
    <div class="aspect-[4/3] w-full max-w-screen-lg">
      <RouterView />
    </div>
  </div>
</template>
