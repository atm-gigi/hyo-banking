<script setup>
  import { onMounted } from 'vue';
  import { useAuthStore } from './stores/auth';
  import DefaultLayout from '@/components/DefaultLayout.vue';

  const authStore = useAuthStore();

  onMounted(() => {
    // 앱 시작 시 로컬 스토리지에서 인증 정보 복원
    authStore.initializeAuth();
  });
</script>

<template>
  <DefaultLayout />
</template>
