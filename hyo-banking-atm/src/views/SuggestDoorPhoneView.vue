<script setup>
  import { onMounted, onUnmounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import suggestDoorPhoneAudio from '@/assets/audio/suggest-door-phone.mp3';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const router = useRouter();
  const audio = ref(new Audio(suggestDoorPhoneAudio));
  const audioStore = useAudioStore();

  const handleEnter = () => {
    router.push({ name: 'home' });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    } else if (event.key === 'Backspace') {
      router.push({ name: 'home' });
    }
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyPress);
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>

<template>
  <StopAudioButton />
  <main class="relative h-screen bg-white flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold">
      ATM 기기 오른쪽, 왼쪽에 위치한 인터폰으로 <br />
      직원의 도움을 받을 수 있습니다.
    </p>
    <img src="@/assets/call-phone.png" alt="" class="w-300 m-auto" />
    <form class="" @onKeyClick="handleKeyClick"></form>
  </main>
</template>
