<script setup>
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const handleAssistBtn = () => {
    router.push('/choose-task');
  };
</script>

<template>
  <main class="relative h-full min-h-screen bg-gray-100 flex flex-col">
    <!-- 메인 버튼 그리드 -->
    <div class="pt-10 pb-32 px-10 grid grid-cols-3 gap-4 w-full max-w-7xl mx-auto flex-1">
      <!-- 혼자 하기 버튼 -->
      <button
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        혼자 하기
      </button>

      <!-- QR 사용 버튼 -->
      <button
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        QR 사용
      </button>

      <!-- 도움 받기 버튼 -->
      <button
        @click="handleAssistBtn"
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        도움 받기
      </button>
    </div>

    <!-- 하단 다른 업무 보기 버튼 -->
    <button
      class="absolute min-w-screen bottom-0 rounded-t-r-xl rounded-t-xl bg-kb-yellow-200 text-white active:bg-kb-brown-300 font-bold py-6 px-10 text-5xl w-full max-w-md transition duration-200 flex items-center justify-center"
    >
      ← 다른 업무 보기
    </button>
  </main>
</template>

<style scoped>
  /* 추가 커스텀 스타일이 필요한 경우 */
</style>
