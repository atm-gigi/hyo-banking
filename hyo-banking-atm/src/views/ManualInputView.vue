<script setup>
  import { onMounted, onUnmounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';

  const route = useRoute();
  const router = useRouter();
  const amount = ref('100');

  const onKeyClick = key => {
    if (key === '지움') {
      amount.value = amount.value.slice(0, -1);
    } else if (key === '정정') {
      amount.value = '';
    } else {
      amount.value += key;
    }
  };

  const handleEnter = () => {
    router.push({
      name: 'input-password',
      query: { task: route.query.task, payment: route.query.payment },
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
  <main class="w-screen h-screen bg-gray-100 p-10">
    <div class="flex flex-row w-full h-full bg-white rounded-lg shadow p-10">
      <!-- 질문 -->
      <div class="w-1/2 flex flex-col justify-center p-10 space-y-10">
        <h1 class="text-3xl font-bold text-black">얼마를 찾으실 건가요?</h1>
        <!-- 입력창 -->
        <div class="text-3xl font-extrabold rounded items-center">
          <span class="items-center text-blue-500 border-r-4 border-blue-500 pr-1">{{
            amount
          }}</span
          ><span>만원</span>
        </div>
      </div>
      <!-- 키패드 -->
      <div class="w-1/2 flex items-center justify-center p-10">
        <KeyPad @keyClick="onKeyClick" />
      </div>
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>

<style scoped></style>
