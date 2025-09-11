<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { onMounted, ref, onUnmounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import selectTaskAudio from '@/assets/audio/select-task.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';
  import flyBear from '@/assets/FlyBear.png';

  const router = useRouter();
  const atmStore = atmTransactionStore();
  const isShowAssistantView = ref(true);
  const audioStore = useAudioStore();
  const isIdleGuide = ref(false);
  const highlightIndex = ref(-1);
  let idleTimer = null;
  let seqTimer = null;

  const handleDepositClick = () => {
    atmStore.setTaskType(TASK_TYPES.DEPOSIT);
    router.push({
      name: 'payment-method',
      query: { task: TASK_TYPES.DEPOSIT },
    });
  };

  const handleWithdrawalClick = () => {
    atmStore.setTaskType(TASK_TYPES.WITHDRAW);
    router.push({
      name: 'payment-method',
      query: { task: TASK_TYPES.WITHDRAW },
    });
  };

  const handleTransferClick = () => {
    atmStore.setTaskType(TASK_TYPES.TRANSFER);
    router.push({ name: 'select-bank', query: { task: TASK_TYPES.TRANSFER } });
  };

  function showGuide() {
    isIdleGuide.value = true;
    startSequence();
  }
  function hideGuide() {
    isIdleGuide.value = false;
    highlightIndex.value = -1;
    if (seqTimer) {
      clearInterval(seqTimer);
      seqTimer = null;
    }
  }
  function startSequence() {
    highlightIndex.value = 0;
    if (seqTimer) clearInterval(seqTimer);
    seqTimer = setInterval(() => {
      highlightIndex.value = (highlightIndex.value + 1) % 3;
    }, 4000);
  }
  function startIdleTimer() {
    if (idleTimer) clearTimeout(idleTimer);
    idleTimer = setTimeout(() => {
      if (!isShowAssistantView.value) showGuide();
    }, 5000);
  }
  function onUserActivity() {
    hideGuide();
    startIdleTimer();
  }

  onMounted(() => {
    setTimeout(() => {
      isShowAssistantView.value = false;
      startIdleTimer();
    }, 3000);
    window.addEventListener('mousemove', onUserActivity, { passive: true });
    window.addEventListener('mousedown', onUserActivity, { passive: true });
    window.addEventListener('keydown', onUserActivity, { passive: true });
    window.addEventListener('touchstart', onUserActivity, { passive: true });
    window.addEventListener('click', onUserActivity, { passive: true });
    audioStore.initAudio(selectTaskAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
  onUnmounted(() => {
    if (idleTimer) clearTimeout(idleTimer);
    if (seqTimer) clearInterval(seqTimer);
    window.removeEventListener('mousemove', onUserActivity);
    window.removeEventListener('mousedown', onUserActivity);
    window.removeEventListener('keydown', onUserActivity);
    window.removeEventListener('touchstart', onUserActivity);
    window.removeEventListener('click', onUserActivity);
  });
</script>

<template>
  <ReplayAudioButton :src="selectTaskAudio" />
  <StopAudioButton />
  <main class="w-full h-full flex item-center justify-center">
    <!-- 첫 화면 -->
    <div
      class="absolute w-full h-full flex flex-col justify-center items-center transition-opacity duration-3000 ease-in-out"
      :class="{ 'opacity-0': !isShowAssistantView }"
    >
      <p class="text-center text-5xl font-bold">
        안녕하세요. <br />고객님의 ATM 이용 도우미 <br />키키 입니다.
      </p>

      <img src="@/assets/introduce.png" alt="캐릭터" class="w-100 m-auto" />
    </div>

    <!-- 거래 선택 화면 -->
    <div
      class="w-full h-full flex flex-row opacity-0 transition-opacity duration-3000 ease-in-out z-10"
      :class="{ 'opacity-100': !isShowAssistantView }"
    >
      <div class="relative w-full h-full flex flex-col justify-center items-center">
        <p class="text-center text-5xl leading-relaxed font-bold">
          이용하고 싶은<br />거래를 눌러주세요
        </p>
        <img src="@/assets/hello.png" alt="캐릭터" class="w-100 h-auto" />
      </div>
      <div class="w-full h-full flex flex-col px-10 justify-around">
        <!-- 돈 넣기 -->
        <div class="relative flex items-center">
          <img
            :src="flyBear"
            alt="가이드 캐릭터"
            class="guide-bear"
            :class="{
              'opacity-100': isIdleGuide && highlightIndex === 0,
              'opacity-0 pointer-events-none': !(isIdleGuide && highlightIndex === 0),
            }"
          />
          <!-- 크기 유지: w-full 또는 flex-1 로 폭 확보 -->
          <TaskButton
            text="돈 넣기"
            :disabled="isShowAssistantView"
            class="w-full py-16"
            :class="{ 'guide-highlight': isIdleGuide && highlightIndex === 0 }"
            @click="handleDepositClick"
          />
        </div>

        <!-- 돈 꺼내기 -->
        <div class="relative flex items-center">
          <img
            :src="flyBear"
            alt="가이드 캐릭터"
            class="guide-bear"
            :class="{
              'opacity-100': isIdleGuide && highlightIndex === 1,
              'opacity-0 pointer-events-none': !(isIdleGuide && highlightIndex === 1),
            }"
          />
          <TaskButton
            text="돈 꺼내기"
            :disabled="isShowAssistantView"
            class="w-full py-16"
            :class="{ 'guide-highlight': isIdleGuide && highlightIndex === 1 }"
            @click="handleWithdrawalClick"
          />
        </div>

        <!-- 돈 보내기 -->
        <div class="relative flex items-center">
          <img
            :src="flyBear"
            alt="가이드 캐릭터"
            class="guide-bear"
            :class="{
              'opacity-100': isIdleGuide && highlightIndex === 2,
              'opacity-0 pointer-events-none': !(isIdleGuide && highlightIndex === 2),
            }"
          />
          <TaskButton
            text="돈 보내기"
            :disabled="isShowAssistantView"
            class="w-full py-16"
            :class="{ 'guide-highlight': isIdleGuide && highlightIndex === 2 }"
            @click="handleTransferClick"
          />
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
  @keyframes blink {
    0%,
    100% {
      opacity: 1;
    }
    50% {
      opacity: 0.35;
    }
  }
  @keyframes float {
    0% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-8px);
    }
    100% {
      transform: translateY(0);
    }
  }

  /* 버튼 점선 하이라이트 */
  .guide-highlight {
    outline: 8px dashed #f6b800;
    outline-offset: 8px;
    animation: blink 1s infinite;
    border-radius: 20px;
  }

  /* 버튼 옆 곰돌이 - 절대 위치로 레이아웃에 영향 X */
  .guide-bear {
    position: absolute;
    left: -150px; /* 버튼 바깥 왼쪽 */
    width: 200px;
    z-index: 1;
    transition:
      opacity 0.4s ease-in-out,
      transform 0.2s ease-in-out;
    animation: float 2.4s ease-in-out infinite;
  }
</style>
