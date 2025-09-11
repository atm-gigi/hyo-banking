// stores/audio.js
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAudioStore = defineStore('audio', () => {
  const stopAudioOn = ref(true); // 버튼 상태
  const audio = ref(null); // 오디오 객체

  const initAudio = src => {
    audio.value = new Audio(src);
  };

  const toggleAudio = () => {
    stopAudioOn.value = !stopAudioOn.value;
    if (stopAudioOn.value) audio.value?.play().catch(() => console.warn('자동 재생 실패'));
    else audio.value?.pause();
  };

  const playAudio = () => {
    audio.value?.play().catch(() => console.warn('자동 재생 실패'));
  };

  const stopAudio = () => {
    if (audio.value) {
      audio.value.pause();
      audio.value.currentTime = 0;
    }
  };

  return { stopAudioOn, audio, initAudio, toggleAudio, playAudio, stopAudio };
});
