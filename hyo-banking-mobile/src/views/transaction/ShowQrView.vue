<script setup>
  import { ref, onMounted, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import QRCode from 'qrcode';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import Modal from '@/components/Modal.vue';
  import { createQrToken } from '@/apis';

  const route = useRoute();
  const router = useRouter();

  const showModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  // 서버에서 받은 데이터
  const qrData = ref(null);
  const isLoading = ref(false);
  const qrCodeUrl = ref('');

  const macroId = computed(() => route.params.id);
  const macroName = computed(() => route.query.macroName || '매크로');

  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  const handleModalConfirm = () => {
    showModal.value = false;
    router.push({ name: 'home' });
  };

  const handleModalCancel = () => {
    showModal.value = false;
  };

  const handleCopyToken = async () => {
    try {
      const jsonString = JSON.stringify(qrData.value, null, 2);
      await navigator.clipboard.writeText(jsonString);
      showAlert('복사 완료', 'JSON 데이터가 클립보드에 복사되었습니다.');
    } catch (error) {
      console.error('복사 실패:', error);
      showAlert('오류', '복사에 실패했습니다.');
    }
  };

  // QR 코드 생성 (JSON 객체를 문자열로 변환)
  const generateQRCode = async qrData => {
    try {
      const options = {
        width: 200,
        margin: 2,
        color: {
          dark: '#000000',
          light: '#FFFFFF',
        },
      };

      // JSON 객체를 문자열로 변환
      const jsonString = JSON.stringify(qrData);
      qrCodeUrl.value = await QRCode.toDataURL(jsonString, options);
    } catch (error) {
      console.error('QR 코드 생성 오류:', error);
      showAlert('오류', 'QR 코드 생성 중 오류가 발생했습니다.');
    }
  };

  // QR 토큰 데이터 로드
  const loadQrData = async () => {
    if (!macroId.value) {
      showAlert('오류', '잘못된 매크로 정보입니다.');
      router.push({ name: 'home' });
      return;
    }

    try {
      isLoading.value = true;

      // 기존 토큰이 있는지 확인
      if (route.query.token) {
        // URL에서 토큰을 받은 경우
        qrData.value = {
          macroId: parseInt(macroId.value),
          qrToken: route.query.token,
        };
        await generateQRCode(qrData.value);
      } else {
        // 서버에서 새 토큰 생성
        const response = await createQrToken(macroId.value);
        qrData.value = response;
        await generateQRCode(qrData.value);
      }
    } catch (error) {
      console.error('QR 토큰 로드 오류:', error);
      showAlert('오류', 'QR 토큰을 가져오는 중 오류가 발생했습니다.');
      router.push({ name: 'home' });
    } finally {
      isLoading.value = false;
    }
  };

  const handleRefreshQr = async () => {
    try {
      isLoading.value = true;
      const response = await createQrToken(macroId.value);
      qrData.value = response;
      await generateQRCode(qrData.value);
      showAlert('새로고침 완료', '새 QR 코드가 생성되었습니다.');
    } catch (error) {
      console.error('QR 토큰 새로고침 오류:', error);
      showAlert('오류', 'QR 토큰 새로고침 중 오류가 발생했습니다.');
    } finally {
      isLoading.value = false;
    }
  };

  onMounted(() => {
    loadQrData();
  });
</script>

<template>
  <main class="w-full h-full flex flex-col">
    <div class="px-5">
      <div class="pt-10 pb-8">
        <BackButton />
      </div>

      <h1 class="text-2xl font-bold mb-2 text-center">매크로 실행</h1>
      <p class="text-gray-600 text-center mb-8">{{ macroName }}</p>

      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="bg-white rounded-2xl p-8 shadow-sm mb-6">
        <div class="text-center">
          <div
            class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto mb-4"
          ></div>
          <p class="text-gray-600">QR 코드를 생성하는 중...</p>
        </div>
      </div>

      <!-- QR 코드 영역 -->
      <div v-else-if="qrData && qrCodeUrl" class="bg-white rounded-2xl p-8 shadow-sm mb-6">
        <div class="text-center">
          <div class="mb-4">
            <img
              :src="qrCodeUrl"
              alt="QR Code"
              class="mx-auto border-2 border-gray-200 rounded-lg"
              style="width: 200px; height: 200px"
            />
          </div>

          <p class="text-sm text-gray-600 mb-4">ATM에서 이 QR 코드를 스캔하세요</p>

          <div class="bg-gray-50 rounded-lg p-3 mb-4">
            <p class="text-xs text-gray-500 mb-1">QR 코드 데이터 (JSON)</p>
            <p class="text-sm font-mono break-all">{{ JSON.stringify(qrData, null, 2) }}</p>
          </div>

          <div class="flex gap-2">
            <button
              @click="handleCopyToken"
              class="flex-1 py-2 bg-gray-200 text-gray-700 rounded-xl font-medium text-sm"
            >
              JSON 복사
            </button>
            <button
              @click="handleRefreshQr"
              :disabled="isLoading"
              class="flex-1 py-2 bg-blue-100 text-blue-700 rounded-xl font-medium text-sm disabled:opacity-50"
            >
              새로고침
            </button>
          </div>
        </div>
      </div>

      <!-- 에러 상태 -->
      <div v-else class="bg-red-50 rounded-2xl p-8 shadow-sm mb-6">
        <div class="text-center">
          <div class="text-red-600 text-4xl mb-4">⚠️</div>
          <p class="text-red-800 font-medium mb-2">QR 코드를 불러올 수 없습니다</p>
          <p class="text-red-600 text-sm">잠시 후 다시 시도해주세요</p>
        </div>
      </div>

      <!-- 사용 안내 -->
      <div class="bg-blue-50 rounded-2xl p-6 mb-6">
        <h3 class="font-bold text-blue-900 mb-3">📱 사용 방법</h3>
        <ol class="text-sm text-blue-800 space-y-2">
          <li>1. ATM에서 "QR 코드 스캔" 메뉴를 선택하세요</li>
          <li>2. 이 화면의 QR 코드를 스캔하세요</li>
          <li>3. 매크로가 자동으로 실행됩니다</li>
          <li>4. QR 코드는 5분 후 만료됩니다</li>
        </ol>
      </div>

      <!-- 주의사항 -->
      <div class="bg-yellow-50 rounded-2xl p-6 mb-6">
        <h3 class="font-bold text-yellow-900 mb-3">⚠️ 주의사항</h3>
        <ul class="text-sm text-yellow-800 space-y-1">
          <li>• QR 코드는 1회만 사용 가능합니다</li>
          <li>• 타인과 공유하지 마세요</li>
          <li>• 만료된 코드는 다시 생성해야 합니다</li>
        </ul>
      </div>
    </div>

    <div class="px-5 pb-10">
      <PrimaryBtn
        class="w-full py-3"
        text="홈으로 돌아가기"
        @click="() => router.push({ name: 'home' })"
      />
    </div>

    <!-- Modal -->
    <Modal
      :isVisible="showModal"
      :title="modalConfig.title"
      :message="modalConfig.message"
      :showCancel="modalConfig.showCancel"
      @confirm="handleModalConfirm"
      @cancel="handleModalCancel"
    />
  </main>
</template>
