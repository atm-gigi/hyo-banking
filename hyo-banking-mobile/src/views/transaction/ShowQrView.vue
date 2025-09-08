<script setup>
  import { ref, onMounted } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { STORAGE_KEYS } from '@/constants';
  import GoBackBtn from '@/components/buttons/GoBackBtn.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';

  const route = useRoute();
  const router = useRouter();
  const transaction = ref(null);

  onMounted(() => {
    loadTransaction();
  });

  const loadTransaction = () => {
    const transactionId = route.params.id;
    if (!transactionId) return;

    const transactions = JSON.parse(localStorage.getItem(STORAGE_KEYS.TRANSACTIONS) || '[]');
    transaction.value = transactions.find(t => t.id === transactionId);
  };

  const handleComplete = () => {
    router.push({ name: 'home' });
  };
</script>

<template>
  <main class="w-full h-full flex flex-col">
    <!-- 헤더 -->
    <div class="px-5 pt-10 pb-6">
      <div class="">
        <GoBackBtn />
      </div>
    </div>

    <!-- QR 코드 표시 -->
    <div v-if="transaction" class="px-5 flex-1 flex flex-col items-center justify-between">
      <!-- QR 코드 영역 -->
      <div class="bg-white rounded-2xl shadow-sm p-8 w-full max-w-sm">
        <div class="text-center">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">QR 코드</h3>
          <div class="bg-gray-100 rounded-xl p-8 mb-4">
            <!-- QR 코드 이미지 또는 플레이스홀더 -->
            <div class="w-48 h-48 mx-auto bg-gray-200 rounded-lg flex items-center justify-center">
              <div class="text-center text-gray-500">
                <svg class="w-16 h-16 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fill-rule="evenodd"
                    d="M3 4a1 1 0 011-1h3a1 1 0 011 1v3a1 1 0 01-1 1H4a1 1 0 01-1-1V4zm2 2V5h1v1H5zM3 13a1 1 0 011-1h3a1 1 0 011 1v3a1 1 0 01-1 1H4a1 1 0 01-1-1v-3zm2 2v-1h1v1H5zM13 4a1 1 0 011-1h3a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1V4zm2 2V5h1v1h-1z"
                    clip-rule="evenodd"
                  ></path>
                </svg>
                <p class="text-sm">QR 코드</p>
              </div>
            </div>
          </div>
          <p class="text-sm text-gray-500">QR 코드를 스캔하여 거래 정보를 확인하세요</p>
        </div>
      </div>

      <!-- 거래 정보 카드 -->

      <!-- 완료 버튼 -->
      <div class="w-full pb-10">
        <PrimaryBtn class="w-full py-3" text="완료" @click="handleComplete" />
      </div>
    </div>

    <!-- 거래 정보 없음 -->
    <div v-else class="px-5 flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-gray-500 text-lg">거래 정보를 찾을 수 없습니다.</p>
        <GoBackBtn text="돌아가기" class="mt-4" />
      </div>
    </div>
  </main>
</template>
