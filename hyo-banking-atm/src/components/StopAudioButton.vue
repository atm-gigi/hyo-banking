<script setup>
  import { useAudioStore } from '@/stores/audio';
  const audioStore = useAudioStore();

  const handleClick = () => {
    audioStore.toggleAudio();
  };
</script>

<template>
  <!-- 부모 컨테이너 안에서 배치 -->
  <div class="absolute top-2 right-120 z-50">
    <button
      @click="handleClick"
      class="relative inline-flex items-center w-40 h-14 rounded-full px-4 transition-colors duration-300 ease-out"
      :class="audioStore.stopAudioOn ? 'bg-kb-yellow-200' : 'bg-gray-300'"
    >
      <span
        class="absolute left-1 top-1 w-12 h-12 bg-white rounded-full shadow-md flex items-center justify-center text-2xl transform transition-transform duration-300 ease-out"
        :class="audioStore.stopAudioOn ? 'translate-x-26' : 'translate-x-0'"
      >
        {{ audioStore.stopAudioOn ? '🔊' : '🔇' }}
      </span>
      <span
        class="absolute font-bold text-lg text-gray-700 pointer-events-none transition-all duration-300"
        :class="audioStore.stopAudioOn ? 'left-5' : 'right-5'"
      >
        {{ audioStore.stopAudioOn ? '소리 끄기' : '소리 켜기' }}
      </span>
    </button>
  </div>
</template>
