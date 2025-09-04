<script setup>
  import { PAYMENT_TYPES, TASK_TYPES } from '@/constants';
  import { computed, onMounted, onUnmounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();
  const isShowAssistantView = ref(true);

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

  const handleEnter = () => {
    if (task.value === '입금') {
      router.push({
        name: 'get-cash',
        query: { task: route.query.task, payment: route.query.payment },
      });
    } else {
      router.push({
        name: 'select-amount',
        query: { task: route.query.task, payment: route.query.payment },
      });
    }
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

  onMounted(() => {
    setTimeout(() => {
      isShowAssistantView.value = false;
    }, 1000);
  });
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col justify-between">
    <!-- 통장 투입 안내 화면 -->
    <div
      class="min-w-screen bottom-0 rounded-b-r-xl rounded-b-xl bg-kb-yellow-200 text-white active:bg-kb-brown-300 font-bold py-6 px-10 text-5xl w-full max-w-md transition duration-200 flex items-center justify-center"
    >
      {{ task }}
    </div>
    <!-- 안내 문구 화면 -->
    <p
      class="absolute inset-0 flex items-center justify-center text-center text-5xl leading-relaxed font-bold transition-opacity duration-1000 ease-in-out"
      :class="{
        'opacity-100 pointer-events-none': isShowAssistantView,
        'opacity-0': !isShowAssistantView,
      }"
    >
      투입구를 화면 속 이미지와 비교해보고 투입구에 {{ payment }} 넣어주세요 <br />

    </p>
    <img
      src=""
      alt="불법복제기사진"
    />

    <!-- 거래 선택 화면 -->
    <div
      class="absolute inset-0 flex flex-col justify-center items-center text-center px-12 transition-opacity duration-1000 ease-in-out z-10"
      :class="{
        'opacity-0': isShowAssistantView,
        'opacity-100 pointer-events-none': !isShowAssistantView,
      }"
    >
      <p class="text-5xl leading-relaxed font-bold">
        만약 불법 복제기가 부착된 것으로 <br />
        의심되는 경우,<br />
        통장과 카드를 넣지 마시고<br />
        즉시 인터폰을 들어<br />
        직원에게 도움을 요청해주세요.
      </p>
      <img src="" alt="" />
    </div>

    <form @submit.prevent="handleEnter">
      <button type="submit"></button>
    </form>
  </main>
</template>
