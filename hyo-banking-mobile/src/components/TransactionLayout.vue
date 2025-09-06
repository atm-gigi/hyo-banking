<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const task = computed(() => {
    if (route.query.task == TASK_TYPES.DEPOSIT) return '입금';
    if (route.query.task == TASK_TYPES.WITHDRAW) return '출금';

    return null;
  });

  const isShowNav = computed(() => {
    return !['start', 'login'].includes(route.name);
  });

  const handleGoBack = () => {
    router.back();
  };

  const handleHomeClick = () => {
    router.push({
      name: 'home',
    });
  };
</script>

<template>
  <div class="relative mx-auto w-md h-screen overflow-y-scroll bg-gray-50">
    <!-- 상단 고정 헤더 -->
    <nav
      v-if="isShowNav"
      class="fixed w-md mx-auto bg-gray-100 top-0 left-0 right-0 h-15 z-50 px-5 flex items-center justify-between opacity-75"
    >
      <button @click="handleGoBack" class="w-full cursor-pointer opacity-100 text-left">
        ← 뒤로가기
      </button>
      <div class="w-full">
        <h2 class="w-full text-center text-xl font-bold">{{ task }}</h2>
      </div>
      <div class="w-full"></div>
    </nav>

    <RouterView class="pt-15 pb-20 bg-white" />

    <!-- 하단 고정 헤더 -->
    <nav
      v-if="isShowNav"
      class="fixed w-md mx-auto bg-gray-100 bottom-0 left-0 right-0 h-20 z-50 flex flex-row justify-evenly"
    >
      <button @click="handleHomeClick" class="cursor-pointer">
        <div>홈</div>
      </button>
      <button>
        <div>설정</div>
      </button>
    </nav>
  </div>
</template>
