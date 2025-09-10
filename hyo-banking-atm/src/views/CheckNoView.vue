<script setup>
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';
  import { ref, onMounted } from 'vue';
  import checkNoAudio from '@/assets/audio/check-no.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();
  const audio = ref(new Audio(checkNoAudio));
  const audioStore = useAudioStore();

  const handleGoBackClick = () => {
    router.push({ name: 'select-bills', query: { task: route.query.task } });
  };
  const handleQuitClick = () => {
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };

  onMounted(() => {
    audioStore.initAudio(checkNoAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
</script>

<template>
  <ReplayAudioButton :src="checkNoAudio" />
  <StopAudioButton />
  <main class="relative h-screen flex-col justify-between p-10 bg-white">
    <p class="text-center text-5xl font-bold mb-5">거래를 취소하시겠습니까?</p>
    <img src="@/assets/cancelled.png" alt="" class="w-120 m-auto" />
    <div class="w-full flex flex-row justify-center mt-auto gap-x-5">
      <TaskButton text="아니요" class="w-full max-w-sm" @click="handleGoBackClick" />
      <TaskButton
        text="취소합니다"
        class="w-full max-w-sm bg-kb-yellow-200"
        @click="handleQuitClick"
      />
    </div>
  </main>
</template>
