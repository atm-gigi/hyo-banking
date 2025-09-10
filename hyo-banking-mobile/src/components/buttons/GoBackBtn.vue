<template>
  <button
    @click="handleClick"
    class="flex items-center hover:bg-gray-100 rounded-full transition-colors py-2 px-3 cursor-pointer"
  >
    <div class="mr-4">
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M15 19l-7-7 7-7"
        ></path>
      </svg>
    </div>
    <span>뒤로가기</span>
  </button>
</template>

<script setup>
  import { useRouter } from 'vue-router';

  const props = defineProps({
    handleGoBack: { type: Function, default: null }, // 커스텀 뒤로가기 함수
  });

  const emit = defineEmits(['click']);
  const router = useRouter();

  const handleClick = () => {
    if (props.handleGoBack) {
      props.handleGoBack();
    } else {
      router.back();
    }
    emit('click');
  };
</script>
