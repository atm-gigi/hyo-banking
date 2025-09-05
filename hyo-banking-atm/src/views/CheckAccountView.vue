<script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';

  const route = useRoute();
  const router = useRouter();
  const accountNumber = ref('123-456-789');

  onMounted(() => {
    setTimeout(() => {}, 500);
  });

  const handleNoClick = () => {
    console.log("'아니요' 버튼 클릭: 계좌번호 재입력 또는 이전 단계로 이동");
    router.push({ name: 'undo-transaction', query: { task: route.query.task } });
  };

  const handleYesClick = () => {
    console.log("'네' 버튼 클릭: 계좌번호 확인 완료, 다음 단계로 이동");
    router.push({ name: 'select-amount', query: { task: route.query.task } });
  };
</script>

<template>
  <main class="w-screen h-screen flex flex-col items-center py-10">
    <div class="space-y-10">
      <h1 class="text-5xl font-bold text-black mb-10">입력하신 계좌번호가 맞나요?</h1>
    </div>

    <div class="bg-kb-yellow-100 rounded-xl py-5 px-10 w-auto">
      <p class="text-4xl font-extrabold text-black">
        {{ accountNumber }}
      </p>
    </div>

    <img src="" alt="계좌번호 확인하는 곰돌이" class="m-auto" />

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
