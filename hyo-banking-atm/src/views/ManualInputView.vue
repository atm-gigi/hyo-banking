<script setup>
  import { onMounted, onUnmounted, ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';

  const route = useRoute();
  const router = useRouter();
  const amount = ref('100');
  const task = ref(route.query.task);

  const title = computed(() => {
    // route.query.task가 'TRANSFER'이면 "얼마를 보내실 건가요?"를,
    // 그렇지 않으면 "얼마를 찾으실 건가요?"를 반환합니다.
    return task.value === 'TRANSFER' ? '얼마를 보내실 건가요?' : '얼마를 찾으실 건가요?';
  });

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
    if (task.value === 'TRANSFER')
      router.push({
        name: 'check-transfer',
        query: { task: route.query.task, payment: route.query.payment },
      });
    else
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
  <main class="w-screen h-screen bg-gray-100">
    <div class="flex flex-row w-full h-full rounded-lg p-5">
      <!-- 질문 -->
      <div class="w-1/2 flex flex-col space-y-5 justify-center">
        <h1 class="text-5xl font-bold text-black">{{ title }}</h1>
        <!-- 입력창 -->
        <div class="text-4xl text-center font-extrabold justify-center rounded items-center mx-10">
          <span class="items-center text-blue-500 border-r-4 border-blue-500 pr-1">{{
            amount
          }}</span>
          <span> 만원</span>
        </div>
      </div>
      <!-- 키패드 -->
      <div class="w-1/2 flex items-center justify-center">
        <KeyPad @keyClick="onKeyClick" />
      </div>
    </div>
    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>

<style scoped></style>
