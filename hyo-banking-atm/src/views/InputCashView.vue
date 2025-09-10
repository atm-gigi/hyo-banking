<script setup>
  import { ref, computed, onMounted, onUnmounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import inputCashAudio from '@/assets/audio/input-cash.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();
  const audio = ref(new Audio(inputCashAudio));
  const audioStore = useAudioStore();

  const handleEnter = () => {
    router.push({
      name: 'check-amount',
      query: { task: route.query.task },
    });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    }
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyPress);
    audioStore.initAudio(inputCashAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();

    setTimeout(() => {
      router.push({
        name: 'check-amount',
        query: { task: route.query.task },
      });
    }, 3000);
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>

<template>
  <ReplayAudioButton :src="inputCashAudio" />
  <StopAudioButton />
  <main class="w-full h-full flex flex-col">
    <div class="w-full h-full flex flex-col gap-y-5 justify-center items-center pt-6">
      <p class="flex text-5xl text-black font-bold text-center">돈 넣는 곳이 열립니다.</p>
      <span class="flex text-5xl text-black font-bold text-center"
        >지폐를 쫙 펴서 넣어주세요.
      </span>
      <img src="@/assets/put-cash.png" alt="돈 넣는 곳이 열립니다." class="w-full flex m-auto" />
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
