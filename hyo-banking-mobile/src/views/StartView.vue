<script setup>
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '../stores/auth';

  const router = useRouter();
  const authStore = useAuthStore();
  const isStartViewAnimating = ref(false);

  const handleStartClick = () => {
    router.push({
      name: 'login',
    });
  };

  const play = async () => {
    authStore.initializeAuth();

    if (authStore.isLoggedIn && authStore.checkTokenExpiry) {
      router.push({
        name: 'home',
      });
    }

    // 1500ms 후 에니메이션 시작
    await new Promise(resolve => {
      setTimeout(() => {
        isStartViewAnimating.value = true;
        resolve();
      }, 1500);
    });

    // 에니메이션 대기
    await new Promise(resolve => {
      setTimeout(() => {
        resolve();
      }, 1000);
    });

    // 로그인 확인시 홈페이지로 이동, 아니면 로그인 페이지로 이동
  };

  onMounted(() => {
    play();
  });
</script>

<template>
  <main class="relative w-full min-h-screen bg-gray-50">
    <!-- 첫 화면 -->
    <div
      class="w-full h-full flex flex-col justify-center items-center transition-opacity duration-1000 ease-in-out"
      :class="{ 'opacity-0': isStartViewAnimating }"
    >
      <p class="text-center text-4xl leading-relaxed font-bold">
        안녕하세요. <br />고객님의 ATM 이용 도우미 <br />000 입니다.
      </p>
      <img src="" alt="캐릭터" />
    </div>

    <!-- 로그인 하러 가기 -->
    <div
      class="w-full h-full flex flex-col justify-center items-center transition-opacity duration-1000 ease-in-out opacity-0 z-10"
      :class="{ 'opacity-100': isStartViewAnimating }"
    >
      <div class="text-center mb-8">
        <h2 class="text-2xl font-bold text-gray-800 mb-4">KB 효뱅킹에 오신 것을 환영합니다</h2>
        <p class="text-gray-600">더 쉬운 은행 서비스를 이용해보세요</p>
      </div>
      <button
        @click="handleStartClick"
        class="border-2 border-kb-yellow-200 text-black px-8 py-4 rounded-2xl text-xl font-bold active:scale-105 transition"
      >
        시작하기
      </button>
    </div>
  </main>
</template>
