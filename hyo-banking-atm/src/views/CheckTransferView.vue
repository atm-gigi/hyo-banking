<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TaskButton from '@/components/TaskButton.vue';

const route = useRoute();
const router = useRouter();

const receiver = {
  name: '김현주',
  bank: 'KB 국민은행',
  account: '123-456-789',
  amount: '10만원',
};

const handleNoClick = () => {
  console.log('Transaction canceled.');
  router.push({ name: 'undo-transaction', query: { task: route.query.task } });
};

const handleYesClick = () => {
  console.log('Transaction confirmed.');
  router.push({ name: 'loading', query: { task: route.query.task } });
};

const receiverDetails = computed(() => [
  { label: '은행', value: receiver.bank },
  { label: '계좌번호', value: receiver.account },
  { label: '금액', value: receiver.amount },
]);


</script>
<template>
  <div class="bg-white h-screen p-8 flex flex-col justify-between">
    <h1 class="text-5xl font-bold text-center">이 분에게 보내는 것이 맞나요?</h1>

    <div class="flex items-center justify-center gap-8 mt-8">
      <img src="" alt="bear" class="" />

      <div>
        <div class="text-3xl font-bold mb-4">{{ receiver.name }}</div>

        <table class="text-left text-lg w-full">
          <tbody>
            <tr
              v-for="detail in receiverDetails"
              :key="detail.label"
              class="border-t last:border-b"
            >
              <td class="py-2 text-gray-600 font-semibold">{{ detail.label }}</td>
              <td class="py-2 pl-4">{{ detail.value }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="w-full h-32 flex flex-row justify-center px-10 gap-x-5">
      <TaskButton text="아니요" class="w-full max-w-sm" @click="handleNoClick" />
      <TaskButton
        text="네 맞습니다"
        class="w-full max-w-sm bg-kb-yellow-200"
        @click="handleYesClick"
      />
    </div>
  </div>
</template>


