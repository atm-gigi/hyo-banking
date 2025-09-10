<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import selectTaskAudio from '@/assets/audio/select-task.mp3';
  import selectTaskAudio2 from '@/assets/audio/select-task2.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const router = useRouter();
  const atmStore = atmTransactionStore();
  const isShowAssistantView = ref(true);
  const audio = ref(new Audio(selectTaskAudio));
  const audioStore = useAudioStore();

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

  onMounted(() => {
    setTimeout(() => {
      isShowAssistantView.value = false;
    }, 1000);
    audioStore.initAudio(selectTaskAudio2);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
</script>

<template>
  <ReplayAudioButton :src="selectTaskAudio" />
  <StopAudioButton />
  <main class="relative h-screen flex flex-col bg-white">
    <!-- 첫 화면 -->
    <div
      class="p-10 absolute w-full h-full flex flex-col justify-center items-center transition-opacity duration-1000 ease-in-out"
      :class="{ 'opacity-0': !isShowAssistantView }"
    >
      <p class="text-center text-5xl leading-relaxed font-bold">
        안녕하세요. <br />고객님의 ATM 이용 도우미 <br />000 입니다.
      </p>
      <img src="@/assets/introduce.png" alt="캐릭터" class="w-100 m-auto" />
    </div>

    <!-- 거래 선택 화면 -->
    <div
      class="w-full h-full flex flex-row justify-between opacity-0 transition-opacity duration-1000 ease-in-out z-10"
      :class="{ 'opacity-100': !isShowAssistantView }"
    >
      <div class="w-full h-full flex flex-col justify-center items-center">
        <p class="text-center text-5xl leading-relaxed font-bold">
          이용하고 싶은<br />거래를 눌러주세요
        </p>
        <img src="@/assets/hello.png" alt="캐릭터" class="w-100 h-auto" />
      </div>
      <div class="w-full h-full flex flex-col px-10 justify-around">
        <TaskButton
          text="돈 넣기"
          :disabled="isShowAssistantView"
          class="py-16"
          @click="handleDepositClick"
        />
        <TaskButton
          text="돈 찾기"
          :disabled="isShowAssistantView"
          class="py-16"
          @click="handleWithdrawalClick"
        />
        <TaskButton
          text="돈 보내기"
          :disabled="isShowAssistantView"
          class="py-16"
          @click="handleTransferClick"
        />
      </div>
    </div>
  </main>
</template>
