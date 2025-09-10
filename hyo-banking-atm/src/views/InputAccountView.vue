<script setup>
  import { onMounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';
  import TaskButton from '@/components/TaskButton.vue';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import inputAccountAudio from '@/assets/audio/input-account.mp3';
  import ReplayAudioButton from '@/components/ReplayAudioButton.vue';
  import StopAudioButton from '@/components/StopAudioButton.vue';
  import { useAudioStore } from '@/stores/audio';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();
  const amount = ref('');
  const audio = ref(new Audio(inputAccountAudio));
  const audioStore = useAudioStore();

  const isAccountFull = computed(() => {
    // YYYYYY-ZZ-ZZZZZC 형식은 총 15자리 (숫자 13 + 하이픈 2)
    // 하이픈이 자동으로 생성되므로, amount.value의 길이가 15가 되면 가득 찬 것으로 판단
    return amount.value.length === 15;
  });

  // 계좌번호를 포맷하는 함수
  const formatAccountNumber = number => {
    // 숫자만 남기고 다른 문자 제거
    const cleaned = number.replace(/[^0-9]/g, '');
    let formatted = cleaned;

    // 요구사항: YYYYYY-ZZ-ZZZZZC (6-2-5-1 형식)
    // 예: 123456-78-90123C (C는 마지막 숫자)

    if (cleaned.length > 6) {
      formatted = cleaned.slice(0, 6) + '-' + cleaned.slice(6);
    }
    if (cleaned.length > 8) {
      formatted = formatted.slice(0, 9) + '-' + formatted.slice(9);
    }
    return formatted;
  };

  const onKeyClick = key => {
    if (key === '정정') {
      // 마지막 글자 제거 후 다시 포맷
      const cleaned = amount.value.replace(/[^0-9]/g, ''); // 현재 값에서 하이픈 제거
      amount.value = formatAccountNumber(cleaned.slice(0, -1));
    } else if (key === '지움') {
      amount.value = '';
    } else {
      // 숫자 입력
      // 현재 입력된 값과 새로 입력될 키를 합쳐서 다시 포맷
      const newRawValue = amount.value.replace(/[^0-9]/g, '') + key;

      if (newRawValue.length <= 13) {
        amount.value = formatAccountNumber(newRawValue);
      }
    }
    console.log(amount.value); // 확인용
  };

  const handleEnter = () => {
    atmStore.setTargetAccountNo(amount.value);
    router.push({
      name: 'check-account',
      query: {
        task: route.query.task,
      },
    });
  };

  onMounted(() => {
    audioStore.initAudio(inputAccountAudio);
    if (audioStore.stopAudioOn) audioStore.playAudio();
  });
</script>

<template>
  <ReplayAudioButton :src="inputAccountAudio" />
  <StopAudioButton />
  <main class="w-full h-full flex items-center justify-center">
    <div class="flex flex-row w-full h-full">
      <div class="w-1/2 flex flex-col justify-center items-center mb-30">
        <h1 class="text-5xl text-center font-bold text-black py-3">
          받는 분의 계좌번호를 <br />
          눌러주세요
        </h1>
        <!-- 입력창 -->
        <div class="text-5xl text-center font-extrabold items-center">
          <span class="items-center text-blue-500 border-r-4 border-blue-500 pr-1">
            {{ amount }}
          </span>
        </div>
      </div>

      <!-- 키패드 -->
      <div class="flex w-1/2 h-full pb-30 p-3">
        <KeyPad @keyClick="onKeyClick" class="w-full h-[400px]" />
      </div>
    </div>

    <div class="fixed bottom-10" :class="{ 'opacity-50 cursor-not-allowed': !isAccountFull }">
      <TaskButton class="w-80 h-18 bg-kb-yellow-200" text="확인" @click="handleEnter"> </TaskButton>
    </div>
  </main>
</template>

<style scoped></style>
