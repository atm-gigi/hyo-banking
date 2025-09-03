<script setup>
  import TaskButton from '@/components/TaskButton.vue';
  import { TASK_TYPES } from '@/constants';
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const isShowAssistantView = ref(true);

  // 1. 버튼 데이터를 배열로 통합하여 관리
  const withdrawalOptions = ref([
    { text: '만원권으로만', billType: 'TEN_THOUSAND' },
    { text: '오만원권으로만', billType: 'FIFTY_THOUSAND' },
    { text: '골고루 섞어서', billType: 'MIXED' },
  ]);

  // 2. 여러 핸들러를 하나로 통합
  const handleOptionClick = selectedOption => {
    console.log('선택된 지폐 종류:', selectedOption.billType);
    // 3. 역할에 맞는 명확한 라우팅 로직으로 수정
    router.push({
      name: 'withdraw-amount', // 다음 단계 (예: 출금 금액 입력)로 이동
      query: {
        task: TASK_TYPES.WITHDRAW,
        bills: selectedOption.billType, // 선택한 지폐 종류를 파라미터로 전달
      },
    });
  };
</script>

<template>
  <main class="relative h-screen bg-gray-100 flex flex-col">
    <div
      class="w-full h-full flex flex-row justify-between opacity-0 transition-opacity duration-1000 ease-in-out"
    >
      <div class="w-full h-full flex flex-col justify-center items-center">
        <p class="text-center text-5xl leading-relaxed font-bold">어떤 지폐로 드릴까요?</p>
        <img src="" alt="캐릭터" />
      </div>

      <div class="w-full h-full flex flex-col px-10 justify-center gap-y-8">
        <TaskButton
          v-for="option in withdrawalOptions"
          :key="option.billType"
          :text="option.text"
          :disabled="isShowAssistantView"
          @click="handleOptionClick(option)"
        />
      </div>
    </div>
  </main>
</template>
