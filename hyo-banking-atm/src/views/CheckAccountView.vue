<script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import TaskButton from '@/components/TaskButton.vue';

  const route = useRoute();
  const router = useRouter();
  const accountNumber = ref('123-456-789');

  onMounted(() => {
    setTimeout(() => {
    }, 500);
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
  <main class="w-screen h-screen bg-white flex flex-col items-center py-16 px-10">
    <div class="absolute top-10 right-10">
      <img src="" alt="KB국민은행 로고" class="h-10" />
    </div>

    <div class="bg-yellow-100 rounded-xl py-5 px-10 mb-10 w-auto">
      <p class="text-4xl font-extrabold text-black tracking-wider">
        {{ accountNumber }}
      </p>
    </div>

    <h1 class="text-5xl font-bold text-black mb-10">입력하신 계좌번호가 맞나요?</h1>

    <div class="mb-20">
      <img
        src=""
        alt="계좌번호 확인하는 곰돌이"
        class="w-64 h-auto"
      />
    </div>
    <div class="w-full h-32 flex flex-row justify-center px-10 gap-x-5">
      <TaskButton text="아니오" class="w-full max-w-sm" @click="handleNoClick" />
      <TaskButton text="네" class="w-full max-w-sm bg-kb-yellow-200" @click="handleYesClick" />
    </div>
  </main>
</template>


