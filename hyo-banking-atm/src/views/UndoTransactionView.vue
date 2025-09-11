<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed, onMounted, onUnmounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import undoTransactionAudio from '@/assets/audio/undo-transaction.mp3';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();
  const audioStore = useAudioStore();

  const task = computed(() => {
    if ((route.query.task || atmStore.task) === TASK_TYPES.DEPOSIT) return '돈 넣기';
    if ((route.query.task || atmStore.task) === TASK_TYPES.WITHDRAW) return '돈 찾기';
    if ((route.query.task || atmStore.task) === TASK_TYPES.TRANSFER) return '돈 보내기';
    if ((route.query.task || atmStore.task) === TASK_TYPES.MACRO) return '매크로';

    return null;
  });

  const handleEnter = () => {
    router.push({ name: 'suggest-door-phone', query: { task: route.query.task } });
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
    audioStore.initAudio(undoTransactionAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>

<template>
  <StopAudioButton />
  <main class="h-full w-full bg-white flex flex-col justify-between">
    <p class="py-10 text-center text-5xl leading-relaxed font-bold leading-relaxed">
      {{ task }}가 취소되었습니다. <br />
      카드와 명세서를 꼭 챙겨가세요.
    </p>
    <img src="@/assets/take-card-bills.png" alt="" class="w-200 m-auto" />
    <form class="" @onKeyClick="handleKeyClick"></form>
  </main>
</template>
