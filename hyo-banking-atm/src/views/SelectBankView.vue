<script setup>
import TaskButton from '@/components/TaskButton.vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const banks = ['국민은행', '신한은행', '우리은행', '부산은행', '대구은행'];

const handleClink = bank => {
  router.push({ name: 'input-account', query: { task: route.query.task, payment: route.query.payment, bank } });
};

</script>
<template>
  <main class="flex h-screen bg-white p-8 items-start justify-between">
    <!-- 왼쪽: 은행 리스트 + 아이콘 -->
    <div class="w-1/2 flex flex-col items-start space-y-4">

      <TaskButton
        v-for="(bank, index) in banks"
        :key="index"
        :text="bank"
        class="w-64 font-smibold h-32"
        @click="handleClink(bank)"
      >
      <img src="" alt="은행 심볼" class="w-10 mb-2" />
        {{ bank }}
      </TaskButton>
    </div>

    <!-- 오른쪽: 설명 영역 -->
    <div class=" w-1/2 flex flex-col items-end">
      <p class="text-2xl font-bold text-black">돈 받으실 분의<br />은행을 골라주세요</p>
    </div>
  </main>
</template>


