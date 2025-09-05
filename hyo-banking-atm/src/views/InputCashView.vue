<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const amount = ref(166000);
  const isShowAssistantView = ref(false);

  const billDenominations = [
    { denomination: 50000, label: '5만원', img: '' },
    { denomination: 10000, label: '1만원', img: '' },
    { denomination: 5000, label: '5천원', img: '' },
    { denomination: 1000, label: '1천원', img: '' },
  ];

  const billCounts = computed(() => {
    const counts = {};
    let remainingAmount = amount.value;

    for (const bill of billDenominations) {
      const denomination = bill.denomination;
      if (remainingAmount >= denomination) {
        counts[denomination] = Math.floor(remainingAmount / denomination);
        remainingAmount %= denomination;
      } else {
        counts[denomination] = 0;
      }
    }
    return counts;
  });

  // 금액을 통화 형식(e.g., 166,000)으로 변환
  const formattedAmount = computed(() => {
    return amount.value.toLocaleString('ko-KR');
  });

  // '예 맞아요' 버튼 클릭 시 실행될 함수
  const handleDepositClick = () => {
    console.log('입금 확인:', amount.value, '원');
    router.push({
      name: 'loading',
      query: { task: route.query.task, payment: route.query.payment },
    });
  };

  // '아니오' 버튼 클릭 시 실행될 함수
  const handleGoBackClick = () => {
    console.log('입금 취소');
    // 이전 페이지로 이동하거나 초기 화면으로 돌아가는 로직 추가
    router.go(-1);
  };
</script>

<template>
  <main class="relative w-screen h-screen bg-gray-100 flex flex-col">
    <p class="text-5xl text-black font-semibold text-center py-10">총 금액 확인</p>

    <div class="rounded-xl p-6 space-y-6 flex-1 flex flex-col">
      <div class="text-center space-y-2">

        <div class="text-3xl font-semibold">입금하신 금액이  <p class="text-3xl font-bold">{{ formattedAmount }}원</p> 맞으신가요?</div>
      </div>

      <div class="grid grid-cols-2 gap-x-4 gap-y-5 justify-items-center items-center my-5">
        <div
          v-for="bill in billDenominations"
          :key="bill.denomination"
          class="flex items-center space-x-3 w-full justify-center"
        >
          <img :src="bill.img" :alt="bill.label" class="w-20" />
          <span class="text-xl font-semibold w-16 text-left">
            {{ billCounts[bill.denomination] }}장
          </span>
        </div>
      </div>

      <div class="w-screen flex justify-center gap-x-5 mt-auto">
        <TaskButton
          text="아니오"
          class="w-full max-w-sm"
          :disabled="isShowAssistantView"
          @click="handleGoBackClick"
        />
        <TaskButton
          text="예 맞아요"
          class="w-full max-w-sm bg-kb-yellow-200"
          :disabled="isShowAssistantView"
          @click="handleDepositClick"
        />
      </div>
    </div>
  </main>
</template>
