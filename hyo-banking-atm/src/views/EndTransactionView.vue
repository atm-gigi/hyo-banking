<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';
  import endTransactionAudio from '@/assets/audio/end-transaction.mp3';

  const route = useRoute();
  const router = useRouter();
  const audioStore = useAudioStore();

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '돈 넣기';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '돈 찾기';
    if (route.query.task === TASK_TYPES.TRANSFER) return '돈 보내기';
    if (route.query.task === TASK_TYPES.MACRO || route.query.task === 'macro')
      return '간편거래 처리';
    return null;
  });

  onMounted(() => {
    setTimeout(() => {
    // $nextTick()을 사용하여 다음 틱에 로직 실행
    this.$nextTick(() => {
      if (task.value === '간편거래 처리') {
        router.push({ name: 'home' });
      } else {
        router.push({ name: 'statement' });
      }
    });
  }, 5000);
    audioStore.initAudio(endTransactionAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
</script>

<template>
  <ReplayAudioButton :src="selectBankAudio" />
  <StopAudioButton />
  <main class="relative h-screen flex flex-col justify-between">
    <div>
      <p class="py-10 text-center text-5xl font-bold">
        {{ task }}가 끝났습니다. <br />
        카드와 명세서를 꼭 챙겨가세요.
      </p>
    </div>
    <img
      src="@/assets/take-card-bills.png"
      alt="카드와 명세서 챙기는 곰돌이"
      class="w-200 m-auto"
    />
  </main>
</template>
