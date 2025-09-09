<script setup>
  import { onMounted, onUnmounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';
  import UserAPI from '@/apis/UserAPI';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const task = ref(route.query.task);

  const title = computed(() => {
    if (task.value === 'transfer') {
      const targetName = atmStore.targetUserName || '정보 없음';
      return `${targetName}님께 돈을 보내고 있어요`;
    } else if (task.value === 'withdraw') {
      const amount = atmStore.formattedAmount || '금액 정보 없음';
      return `${amount}를 찾고 있어요`;
    } else {
      // 'DEPOSIT' 또는 그 외의 경우
      const amount = atmStore.formattedAmount || '금액 정보 없음';
      return `${amount}을 계좌에 넣고 있어요`;
    }
  });

  const handleEnter = async () => {
    const userId = await UserAPI.getUserId(atmStore.accountNo, atmStore.bankCode);
    console.log('User ID for balance check:', userId);
    const balance = await UserAPI.getBalance(userId);
    console.log('Current balance:', balance);
    router.push({
      name: 'end-transaction',
      query: { task: route.query.task },
    });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    }
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyPress);
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyPress);
  });
</script>

<template>
  <div class="relative h-screen flex flex-col justify-between p-10">
    <p class="text-center text-5xl font-bold">
      {{ title }}
    </p>
    <img src="@/assets/loading.png" alt="곰돌이" class="m-auto w-200" />
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </div>
</template>
