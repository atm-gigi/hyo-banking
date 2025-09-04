<script setup>
  import { onMounted, onUnmounted, ref, watchEffect } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import KeyPad from '@/components/KeyPad.vue';

  const route = useRoute();
  const router = useRouter();

  const password = ref('');

  watchEffect(() => {
    if (password.value.length == 4) {
      router.push({
        name: 'check-amount',
        query: { task: route.query.task, payment: route.query.payment },
      });
    }
  });

  const onKeyClick = key => {
    if (key === '지움') {
      password.value = password.value.slice(0, -1);
    } else if (key === '정정') {
      password.value = '';
    } else {
      password.value += key;
    }
  };

  const handleEnter = () => {
    router.push({
      name: 'check-amount',
      query: { task: route.query.task, payment: route.query.payment },
    });
  };

  const handleKeyPress = event => {
    if (event.key === 'Enter') {
      handleEnter();
    } else if (event.key === 'Backspace') {
      router.push({
        name: 'error',
        query: { task: route.query.task },
      });
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
  <main class="w-screen h-screen bg-gray-100 flex items-center justify-center p-4">
    <div
      class="w-full max-w-2xl h-auto bg-white rounded-lg shadow-xl flex flex-col items-center p-10"
    >
      <div class="flex flex-col items-center justify-center space-y-8 mb-10">
        <h1 class="text-4xl font-bold text-black">비밀번호를 입력해주세요</h1>
        <div class="text-5xl font-extrabold tracking-widest text-gray-800">
          {{ '*'.repeat(password.length) || '----' }}
        </div>
      </div>

      <div class="w-full max-w-sm">
        <KeyPad @keyClick="onKeyClick" />
      </div>
    </div>
  </main>
</template>

<style lang="scss" scoped></style>
