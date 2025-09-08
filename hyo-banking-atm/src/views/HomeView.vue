<script setup>
  import { onMounted} from 'vue';
  import { useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const router = useRouter();
  const atmStore = atmTransactionStore();

onMounted(() => {
    // Reset the store when arriving at the home view
    atmStore.resetTransaction();
  });

  const handleAssistBtn = () => {
    router.push('/select-task');
  };
</script>

<template>
  <main class="relative h-full min-h-screen bg-gray-100 flex flex-col">
    <!-- 메인 버튼 그리드 -->
    <div class="pt-10 pb-32 px-10 grid grid-cols-2 gap-4 w-full max-w-7xl mx-auto flex-1">
      <!-- QR 사용 버튼 -->
      <button
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        QR 사용
      </button>

      <button
        @click="handleAssistBtn"
        class="bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        혼자 하기
      </button>
    </div>
  </main>
</template>

<style scoped></style>
