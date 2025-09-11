<script setup>
  import { onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import StopAudioButton from '@/components/StopAudioButton.vue';

  const router = useRouter();
  const atmStore = atmTransactionStore();

  onMounted(() => {
    // Reset the store when arriving at the home view
    atmStore.resetTransaction();
  });

  const handleAssistBtn = () => {
    router.push('/select-task');
  };
  const toUseQrToken = () => {
    router.push('/qr-guide');
  };
</script>

<template>
  <StopAudioButton />
  <main class="relative h-full min-h-screen flex flex-col">
    <!-- 메인 버튼 그리드 -->
    <div class="pt-10 pb-32 px-10 grid grid-cols-2 gap-4 w-full max-w-7xl mx-auto flex-1">
      <!-- QR 사용 버튼 -->
      <button
        @click="toUseQrToken"
        class="bg-kb-brown-100 shadow-md hover:shadow-lg active:bg-kb-brown-300 active:shadow-inner active:scale-95 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        QR 사용
      </button>

      <button
        @click="handleAssistBtn"
        class="bg-kb-brown-100 active:bg-kb-brown-300 shadow-md hover:shadow-lg active:shadow-inner active:scale-95 text-white font-bold rounded-xl text-6xl transition duration-300 flex items-center justify-center cursor-pointer"
      >
        혼자 하기
      </button>
    </div>
  </main>
</template>

<style scoped></style>
