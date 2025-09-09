<script setup>
  import { computed, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';
  import UserAPI from '@/apis/UserAPI';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const accountNumber = computed(() => atmStore.targetAccountNo);
  const bankName = computed(() => atmStore.targetBankCode);
  const receiverName = computed(() => atmStore.targetUserName);
  console.log('receiverName:', receiverName.value);

  onMounted(async () => {
    try {
      // 4. 스토어에 저장된 계좌 정보로 사용자 이름을 조회합니다.
      const userId = await UserAPI.getUserId(accountNumber.value, bankName.value);
      console.log('수취인 정보 조회 성공:', userId);
    } catch (error) {
      console.error('수취인 정보 조회에 실패했습니다:', error);
      atmStore.setTargetUserName('조회 실패'); // 에러 발생 시 피드백
    }
  });

  const handleNoClick = () => {
    console.log("'아니요' 버튼 클릭: 계좌번호 재입력 또는 이전 단계로 이동");
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };

  const handleYesClick = () => {
    console.log("'네' 버튼 클릭: 계좌번호 확인 완료, 다음 단계로 이동");
    router.push({ name: 'select-amount', query: { ...route.query } });
  };
</script>

<template>
  <main class="w-screen h-screen flex flex-col items-center p-10 bg-white">
    <div class="text-center">
      <div class="mt-8 flex items-baseline justify-center gap-x-4">
        <p class="text-5xl font-bold">{{ bankName }}</p>
        <p class="text-5xl font-bold bg-yellow-100 px-4 py-2 rounded-lg tracking-wider">
          {{ accountNumber }}
        </p>
      </div>
      <h1 class="text-5xl font-semibold leading-relaxed">
        의 계좌주가
        <span class="text-6xl font-bold text-blue-600">{{ receiverName }}</span> 님 맞나요?
      </h1>
    </div>

    <img src="@/assets/check-account.png" alt="계좌번호 확인하는 곰돌이" class="w-92" />

    <div class="w-full flex flex-row justify-center gap-5">
      <TaskButton text="아니오" class="w-full max-w-sm text-center" @click="handleNoClick" />
      <TaskButton
        text="네"
        class="w-full max-w-sm bg-kb-yellow-200 text-center"
        @click="handleYesClick"
      />
    </div>
  </main>
</template>
