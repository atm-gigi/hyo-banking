<script setup>
  import { TASK_TYPES } from '@/constants';
  import { computed, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';

  const route = useRoute();
  const router = useRouter();

  const task = computed(() => {
    if (route.query.task === TASK_TYPES.DEPOSIT) return '돈 넣기';
    if (route.query.task === TASK_TYPES.WITHDRAW) return '돈 찾기';
    if (route.query.task === TASK_TYPES.TRANSFER) return '돈 보내기';
    if (route.query.task === TASK_TYPES.MACRO) return '매크로 처리';
    return null;
  });

  onMounted(() => {
    setTimeout(() => {
      if (task.value === '매크로 처리') router({ name: 'home' });
      else router.push({ name: 'statement' });
    }, 5000);
  });
</script>

<template>
  <main class="relative h-screen flex flex-col justify-between">
    <div>
      <p class="py-10 text-center text-5xl font-bold">
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
