<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const isShowAssistantView = ref(true);

  const handleDepositClick = () => {
    router.push({
      name: 'payment-method',
      query: { task: TASK_TYPES.DEPOSIT },
    });
  };

  const handleWithdrawalClick = () => {
    router.push({
      name: 'payment-method',
      query: { task: TASK_TYPES.WITHDRAW },
    });
  };

  const handleTransferClick = () => {
    router.push({ name: 'select-bank', query: { task: TASK_TYPES.TRANSFER } });
  };

  onMounted(() => {
    setTimeout(() => {
      isShowAssistantView.value = false;
    }, 1000);
  });
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col">
    <!-- 첫 화면 -->
    <div
      class="absolute bg-gray-100 w-full h-full flex flex-col justify-center items-center transition-opacity duration-1000 ease-in-out"
      :class="{ 'opacity-0': !isShowAssistantView }"
    >
      <p class="text-center text-5xl leading-relaxed font-bold">
        안녕하세요. <br />고객님의 ATM 이용 도우미 <br />000 입니다.
      </p>
      <img src="" alt="캐릭터" />
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
        <img src="" alt="캐릭터" />
      </div>
      <div class="w-full h-full flex flex-col px-10 justify-around">
        <TaskButton text="돈 넣기" :disabled="isShowAssistantView" @click="handleDepositClick" />
        <TaskButton text="돈 찾기" :disabled="isShowAssistantView" @click="handleWithdrawalClick" />
        <TaskButton text="돈 보내기" :disabled="isShowAssistantView" @click="handleTransferClick" />
      </div>
    </div>
  </main>
</template>
