<script setup>
  import { ref, onUnmounted } from 'vue';

  const props = defineProps({
    src: { type: String, required: true },
  });

  const isPlaying = ref(false);
  const progress = ref(0); // 배경 애니메이션 진행
  const audio = ref(null);
  let intervalId = null;

  // 재생/일시정지 토글
  const toggleAudio = () => {
    if (!audio.value) {
      audio.value = new Audio(props.src);
      audio.value.addEventListener('ended', () => {
        isPlaying.value = false;
        progress.value = 0;
        clearInterval(intervalId);
      });
    }

    if (isPlaying.value) {
      audio.value.pause();
      isPlaying.value = false;
      clearInterval(intervalId);
    } else {
      audio.value.currentTime = 0;
      audio.value
        .play()
        .then(() => {
          isPlaying.value = true;
          progress.value = 0;

          // 배경 애니메이션 진행
          intervalId = setInterval(() => {
            if (audio.value && audio.value.duration) {
              progress.value = (audio.value.currentTime / audio.value.duration) * 100;
            }
          }, 100);
        })
        .catch(() => {
          console.warn('브라우저 정책 때문에 재생이 막혔습니다.');
        });
    }
  };

  onUnmounted(() => {
    if (audio.value) {
      audio.value.pause();
      audio.value.currentTime = 0;
      audio.value = null;
    }
    clearInterval(intervalId);
  });
</script>

<template>
  <button
    @click="toggleAudio"
    class="fixed top-4 left-4 flex items-center gap-4 px-5 py-3 rounded-full text-gray-800 font-semibold text-base active:scale-95 transition-all duration-300 ease-out z-50 overflow-hidden relative border-2 border-yellow-400 h-14"
    :style="{
      background: `linear-gradient(to right, #FACC15 ${progress}%, white ${progress}%)`,
    }"
  >
    <!-- 재생/일시정지 아이콘 -->
    <span class="w-6 h-6">
      <svg
        v-if="!isPlaying"
        xmlns="http://www.w3.org/2000/svg"
        fill="currentColor"
        viewBox="0 0 24 24"
      >
        <path d="M8 5v14l11-7z" />
      </svg>
      <svg v-else xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 24 24">
        <path d="M6 19h4V5H6zm8-14v14h4V5z" />
      </svg>
    </span>

    <!-- 텍스트 -->
    <span class="font-bold">안내 다시 듣기</span>
  </button>
</template>
