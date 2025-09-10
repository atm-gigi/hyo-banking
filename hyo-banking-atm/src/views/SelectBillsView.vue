<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import selectBillsAudio from '@/assets/audio/select-bills.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const router = useRouter();
  const audio = ref(new Audio(selectBillsAudio));
  const audioStore = useAudioStore();

  const withdrawalOptions = ref([
    { text: '만원권으로만', billType: 'TEN_THOUSAND' },
    { text: '오만원권으로만', billType: 'FIFTY_THOUSAND' },
    { text: '골고루 섞어서', billType: 'MIXED' },
  ]);

  const handleOptionClick = selectedOption => {
    console.log('선택된 지폐 종류:', selectedOption.billType);
    router.push({
      name: 'loading',
      query: {
        task: TASK_TYPES.WITHDRAW,
        bills: selectedOption.billType, // 선택한 지폐 종류를 파라미터로 전달
      },
    });
  };
  onMounted(() => {
    audioStore.initAudio(selectBillsAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
</script>

<template>
  <ReplayAudioButton :src="selectBillsAudio" />
  <StopAudioButton />
  <main class="relative h-screen flex flex-col bg-white">
    <div class="w-full h-full flex flex-row justify-between">
      <div class="w-full h-full flex flex-col justify-center items-center">
        <p class="text-center text-5xl font-bold">어떤 지폐로 드릴까요?</p>
        <img src="@/assets/select-bills.png" alt="캐릭터" class="w-full" />
      </div>

      <div class="w-full h-full flex flex-col px-10 justify-center gap-y-8">
        <TaskButton
          v-for="option in withdrawalOptions"
          :key="option.billType"
          :text="option.text"
          class="py-16"
          @click="handleOptionClick(option)"
        />
      </div>
    </div>
  </main>
</template>
