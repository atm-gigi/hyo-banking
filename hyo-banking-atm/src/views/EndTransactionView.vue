<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed, onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '돈 넣기';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '돈 찾기';
    if (route.query.task === TASK_TYPES.TRANSFER) return '돈 보내기';
    return null;
  });

  const receiver = ref({
    name: '',
    bank: '',
    account: '',
    amount: '',
  });

  onMounted(() => {
    const { name, bank, account, amount } = route.query;
    receiver.value = { name, bank, account, amount };

    setTimeout(() => {
      router.push({ name: 'home' });
    }, 5000);
  });
</script>

<template>
  <main class="bg-white relative h-screen flex flex-col justify-between">
    <div v-if="task === '돈 보내기'">
      <p class="py-10 text-center text-5xl leading-relaxed font-bold">
        {{ receiver.name }}님에게<br />
        {{ receiver.amount }}를 보냈습니다.
      </p>
      <p class="py-10 text-center text-5xl leading-relaxed font-bold">
        {{ task }}가 끝났습니다. <br />
        카드와 명세서를 꼭 챙겨가세요.
      </p>
    </div>
    <div v-else>
      <p class="py-10 text-center text-5xl leading-relaxed font-bold">
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
