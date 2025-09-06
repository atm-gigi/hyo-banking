<script setup>
  import { useAuthStore } from '../stores/auth';
  import { useRouter } from 'vue-router';
  import { maskSensitiveInfo } from '../utils/auth';

  const authStore = useAuthStore();
  const router = useRouter();
</script>

<template>
  <main class="relative w-full min-h-screen pb-20">
    <!-- 메인 콘텐츠 -->
    <div class="max-w-md mx-auto p-4">
      <!-- 환영 메시지 -->
      <div class="bg-white rounded-2xl p-6 mb-6 shadow-sm">
        <h2 class="text-2xl font-bold text-kb-brown-200 mb-2">
          안녕하세요, {{ authStore.userInfo?.name || '사용자' }}님
        </h2>

        <!-- 계좌 정보 -->
        <div class="rounded-lg p-4">
          <div class="text-sm text-kb-brown-200 mb-1">주계좌</div>
          <div class="font-bold text-kb-brown-200">
            {{ maskSensitiveInfo(authStore.userInfo?.accountNumber, 'account') }}
          </div>
          <div class="text-sm text-kb-gray-100">
            {{ authStore.userInfo?.bankName || '국민은행' }}
          </div>
        </div>
        <div>
          <button
            class="w-full border-2 bg-kb-yellow-200 text-white py-2 text-center rounded-2xl cursor-pointer font-bold text-xl hover:scale-105 active:scale-95 active:brightness-90 transition"
          >
            주 계좌 변경하기
          </button>
        </div>
      </div>

      <!-- 메뉴 버튼들 -->
      <div class="flex flex-col gap-3 bg-white p-6 rounded-2xl shadow-sm">
        <button>
          <div class="flex flex-row justify-start items-center">
            <img
              class="size-12"
              width="48"
              height="48"
              src="https://img.icons8.com/color/48/qr-code--v1.png"
              alt="qr-code--v1"
            />
            <span class="font-bold pl-5">QR 찍기</span>
          </div>
        </button>
        <button>
          <div class="flex flex-row justify-start items-center">
            <img
              width="48"
              height="48"
              src="https://img.icons8.com/fluency/48/money.png"
              alt="money"
            />
            <span class="font-bold pl-5">출금</span>
          </div>
        </button>
      </div>
    </div>
  </main>
</template>
