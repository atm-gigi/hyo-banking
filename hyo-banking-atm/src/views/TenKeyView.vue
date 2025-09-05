<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { PAYMENT_TYPES, TASK_TYPES } from '@/constants';
  import { computed, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const money = ref(0);

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '입금';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '출금';
    return null;
  });

  const payment = computed(() => {
    if (route.query.payment === PAYMENT_TYPES.CARD) return '카드';
    if (route.query.payment === PAYMENT_TYPES.BANKBOOK) return '통장';
    return null;
  });

  const handleNumberClick = num => {
    if (money.value === 0) {
      money.value = num;
    } else {
      money.value = money.value * 10 + num;
    }
  };

  const handleClear = () => {
    money.value = 0;
  };

  const handleDelete = () => {
    money.value = Math.floor(money.value / 10);
  };

  const handleConfirm = () => {
    // 다음 화면으로 이동하는 로직
    console.log('입력된 금액:', money.value);
  };
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col">
    <div
      class="min-w-screen bottom-0 rounded-b-r-xl rounded-b-xl bg-kb-yellow-200 text-white active:bg-kb-brown-300 font-bold py-6 px-10 text-5xl w-full max-w-md transition duration-200 flex items-center justify-center"
    >
      {{ payment }} {{ task }}
    </div>
    <div class="w-full h-full flex flex-row justify-between">
      <!-- 입력한 숫자 -->
      <div class="w-full h-full flex flex-col justify-center items-center">
        <img src="" alt="캐릭터" />
        <p class="text-center text-8xl leading-relaxed font-bold text-kb-brown-100">
          {{ money.toLocaleString() }}만원
        </p>
      </div>

      <!-- 숫자패드 -->
      <div class="w-full h-full flex flex-col px-10 justify-between py-10">
        <!-- 숫자 버튼들 -->
        <div class="grid grid-cols-3 gap-4 mb-4 h-full">
          <button
            v-for="num in [7, 8, 9]"
            :key="num"
            @click="handleNumberClick(num)"
            class="w-full h-full bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-4xl transition duration-300 py-6"
          >
            {{ num }}
          </button>
        </div>
        <div class="grid grid-cols-3 gap-4 mb-4 h-full">
          <button
            v-for="num in [4, 5, 6]"
            :key="num"
            @click="handleNumberClick(num)"
            class="w-full h-full bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-4xl transition duration-300 py-6"
          >
            {{ num }}
          </button>
        </div>
        <div class="grid grid-cols-3 gap-4 mb-4 h-full">
          <button
            v-for="num in [1, 2, 3]"
            :key="num"
            @click="handleNumberClick(num)"
            class="w-full h-full bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-4xl transition duration-300 py-6"
          >
            {{ num }}
          </button>
        </div>

        <div class="grid grid-cols-3 gap-4 h-full">
          <button
            @click="handleNumberClick(0)"
            class="w-full h-full bg-kb-brown-100 active:bg-kb-brown-300 active:scale-105 text-white font-bold rounded-xl text-4xl transition duration-300 py-6"
          >
            0
          </button>
          <button
            @click="handleClear"
            class="w-full h-full bg-red-600 active:bg-red-600 active:scale-105 text-white font-bold rounded-xl text-3xl transition duration-300 py-6"
          >
            다시하기
          </button>
          <button
            @click="handleDelete"
            class="w-full h-full bg-yellow-500 active:bg-yellow-600 active:scale-105 text-white font-bold rounded-xl text-3xl transition duration-300 py-6"
          >
            지우기
          </button>
        </div>

        <div class="flex flex-row gap-4 h-full w-full pt-5">
          <TaskButton class="w-full py-2" text="취소" />
          <TaskButton class="w-full py-2" text="확인" />
        </div>
      </div>
    </div>
  </main>
</template>
