<script setup>
  import { computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const isShowNav = computed(() => {
    return !['start', 'login'].includes(route.name);
  });

  const handleHomeClick = () => {
    router.push({
      name: 'home',
    });
  };
</script>

<template>
  <div class="relative mx-auto w-md h-screen overflow-y-scroll bg-gray-50">
    <RouterView :class="{ 'pb-20': isShowNav }" />

    <!-- 하단 고정 헤더 -->
    <nav
      v-if="isShowNav"
      class="fixed w-md mx-auto bg-gray-100 border-t-1 border-t-gray-300 bottom-0 left-0 right-0 h-20 z-50 flex flex-row justify-evenly"
    >
      <button
        @click="handleHomeClick"
        class="cursor-pointer flex flex-col justify-center items-center"
      >
        <img
          class="size-6 opacity-50"
          width="48"
          height="48"
          src="https://img.icons8.com/fluency-systems-regular/48/home--v1.png"
          alt="home--v1"
        />
        <span class="text-gray-700">홈</span>
      </button>
      <button class="cursor-pointer flex flex-col items-center justify-center">
        <img
          class="size-6 opacity-50"
          width="48"
          height="48"
          src="https://img.icons8.com/fluency-systems-regular/48/settings--v1.png"
          alt="settings--v1"
        />
        <span class="text-gray-700">설정</span>
      </button>
    </nav>
  </div>
</template>
