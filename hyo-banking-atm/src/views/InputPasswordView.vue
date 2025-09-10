<script setup>
  import { onMounted, onUnmounted, ref, watchEffect } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';
  import inputPasswordAudio from '@/assets/audio/input-password.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();

  const password = ref('');
  const audio = ref(new Audio(inputPasswordAudio));
  const audioStore = useAudioStore();

  watchEffect(() => {
    if (password.value.length == 4) {
      router.push({
        name: 'select-bills',
        query: { task: route.query.task, payment: route.query.payment },
      });
    }
  });

  const onKeyClick = key => {
    if (key === '지움') {
      password.value = password.value.slice(0, -1);
    } else if (key === '정정') {
      password.value = '';
    } else {
      password.value += key;
    }
  };

  const handleEnter = () => {
    router.push({
      name: 'select-bills',
      query: { task: route.query.task, payment: route.query.payment },
    });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    } else if (event.key === 'Backspace') {
      router.push({
        name: 'error',
        query: { task: route.query.task },
      });
    }
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyPress);
    audioStore.initAudio(inputPasswordAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>
<template>
  <ReplayAudioButton :src="inputPasswordAudio" />
  <StopAudioButton />
  <main class="w-screen h-screen flex items-center justify-center">
    <div class="flex flex-col justify-between mt-5 mb-12">
      <div class="flex flex-col items-center justify-center">
        <h1 class="text-4xl font-bold text-black">비밀번호를 입력해주세요</h1>
        <div class="text-5xl mt-2 mb-4 font-extrabold tracking-widest text-gray-800">
          {{ '*'.repeat(password.length) || '----' }}
        </div>
      </div>

      <div class="w-full max-w-lg">
        <KeyPad @keyClick="onKeyClick" />
      </div>
    </div>
  </main>
</template>

<style lang="scss" scoped></style>
