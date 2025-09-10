<template>
  <main class="w-full h-full flex flex-col">
    <div class="px-5 pt-10 pb-6">
      <BackButton />
      <h1 class="text-2xl font-bold">QR 코드 예제</h1>
    </div>

    <div class="px-5 flex-1">
      <!-- 탭 메뉴 -->
      <div class="flex bg-gray-100 rounded-lg p-1 mb-6">
        <button
          @click="activeTab = 'generate'"
          :class="[
            'flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors',
            activeTab === 'generate'
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-600 hover:text-gray-900',
          ]"
        >
          QR 생성
        </button>
        <button
          @click="activeTab = 'scan'"
          :class="[
            'flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors',
            activeTab === 'scan'
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-600 hover:text-gray-900',
          ]"
        >
          QR 스캔
        </button>
      </div>

      <!-- QR 생성 탭 -->
      <div v-if="activeTab === 'generate'" class="space-y-6">
        <!-- 입력 섹션 -->
        <div class="bg-white rounded-2xl shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-700 mb-4">QR 코드 생성</h2>

          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2"> 텍스트 입력 </label>
              <TextInput
                v-model="qrText"
                placeholder="QR 코드로 변환할 텍스트를 입력하세요"
                @input="generateQR"
              />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2"> 크기 (px) </label>
                <select
                  v-model="qrSize"
                  @change="generateQR"
                  class="w-full p-2 border border-gray-300 rounded-lg"
                >
                  <option value="128">128px</option>
                  <option value="256">256px</option>
                  <option value="512">512px</option>
                </select>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2"> 색상 </label>
                <select
                  v-model="qrColor"
                  @change="generateQR"
                  class="w-full p-2 border border-gray-300 rounded-lg"
                >
                  <option value="#000000">검정</option>
                  <option value="#2563eb">파랑</option>
                  <option value="#dc2626">빨강</option>
                  <option value="#059669">초록</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <!-- QR 코드 표시 -->
        <div v-if="qrCodeUrl" class="bg-white rounded-2xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">생성된 QR 코드</h3>
          <div class="text-center">
            <img
              :src="qrCodeUrl"
              :alt="qrText"
              class="mx-auto border-2 border-gray-200 rounded-lg"
              :style="{ width: qrSize + 'px', height: qrSize + 'px' }"
            />
            <p class="mt-4 text-sm text-gray-600 break-all">{{ qrText }}</p>
          </div>

          <div class="mt-4 flex gap-2">
            <button
              @click="downloadQR"
              class="flex-1 py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              다운로드
            </button>
            <button
              @click="copyQRText"
              class="flex-1 py-2 px-4 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
            >
              텍스트 복사
            </button>
          </div>
        </div>
      </div>

      <!-- QR 스캔 탭 -->
      <div v-if="activeTab === 'scan'" class="space-y-6">
        <!-- 스캔 섹션 -->
        <div class="bg-white rounded-2xl shadow-sm p-6">
          <h2 class="text-lg font-semibold text-gray-700 mb-4">QR 코드 스캔</h2>

          <div v-if="!isScanning" class="text-center">
            <button
              @click="startScanning"
              class="w-full py-3 px-4 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium"
            >
              카메라로 QR 스캔 시작
            </button>
          </div>

          <div v-else class="space-y-4">
            <!-- QR 리더 컴포넌트 -->
            <div class="relative">
              <QrStream
                @decode="onDecode"
                @init="onInit"
                class="w-full h-64 bg-gray-100 rounded-lg"
              />
              <div
                class="absolute inset-0 border-2 border-green-500 rounded-lg pointer-events-none"
              >
                <div
                  class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-32 h-32 border-2 border-white rounded-lg"
                ></div>
              </div>
            </div>

            <button
              @click="stopScanning"
              class="w-full py-2 px-4 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
            >
              스캔 중지
            </button>
          </div>
        </div>

        <!-- 스캔 결과 -->
        <div v-if="scannedText" class="bg-white rounded-2xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">스캔 결과</h3>
          <div class="bg-gray-50 rounded-lg p-4">
            <p class="text-sm text-gray-600 mb-2">스캔된 텍스트:</p>
            <p class="break-all text-gray-900 font-mono">{{ scannedText }}</p>
          </div>

          <div class="mt-4 flex gap-2">
            <button
              @click="copyScannedText"
              class="flex-1 py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              복사
            </button>
            <button
              @click="clearScannedText"
              class="flex-1 py-2 px-4 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors"
            >
              지우기
            </button>
          </div>
        </div>

        <!-- 스캔 히스토리 -->
        <div v-if="scanHistory.length > 0" class="bg-white rounded-2xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">스캔 히스토리</h3>
          <div class="space-y-2 max-h-40 overflow-y-auto">
            <div
              v-for="(item, index) in scanHistory"
              :key="index"
              class="flex items-center justify-between p-2 bg-gray-50 rounded-lg"
            >
              <span class="text-sm text-gray-700 truncate flex-1 mr-2">{{ item }}</span>
              <button
                @click="copyHistoryText(item)"
                class="text-xs px-2 py-1 bg-blue-100 text-blue-600 rounded hover:bg-blue-200 transition-colors"
              >
                복사
              </button>
            </div>
          </div>
          <button
            @click="clearHistory"
            class="mt-3 w-full py-2 px-4 bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors text-sm"
          >
            히스토리 지우기
          </button>
        </div>
      </div>
    </div>

    <!-- 알림 모달 -->
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

<script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import QRCode from 'qrcode';
  import { QrStream } from 'vue3-qr-reader';
  import TextInput from '@/components/TextInput.vue';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';

  const router = useRouter();

  // 탭 상태
  const activeTab = ref('generate');

  // QR 생성 관련
  const qrText = ref('Hello QR Code!');
  const qrSize = ref(256);
  const qrColor = ref('#000000');
  const qrCodeUrl = ref('');

  // QR 스캔 관련
  const isScanning = ref(false);
  const scannedText = ref('');
  const scanHistory = ref([]);

  // 모달 관련
  const showModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  // QR 코드 생성
  const generateQR = async () => {
    if (!qrText.value.trim()) {
      qrCodeUrl.value = '';
      return;
    }

    try {
      const options = {
        width: qrSize.value,
        margin: 2,
        color: {
          dark: qrColor.value,
          light: '#FFFFFF',
        },
      };

      qrCodeUrl.value = await QRCode.toDataURL(qrText.value, options);
    } catch (error) {
      console.error('QR 코드 생성 오류:', error);
      showAlert('오류', 'QR 코드 생성 중 오류가 발생했습니다.');
    }
  };

  // QR 코드 다운로드
  const downloadQR = () => {
    if (!qrCodeUrl.value) return;

    const link = document.createElement('a');
    link.download = `qrcode-${Date.now()}.png`;
    link.href = qrCodeUrl.value;
    link.click();
  };

  // QR 텍스트 복사
  const copyQRText = async () => {
    try {
      await navigator.clipboard.writeText(qrText.value);
      showAlert('복사 완료', '텍스트가 클립보드에 복사되었습니다.');
    } catch (error) {
      console.error('복사 오류:', error);
      showAlert('오류', '복사 중 오류가 발생했습니다.');
    }
  };

  // 스캔 시작
  const startScanning = () => {
    isScanning.value = true;
    scannedText.value = '';
  };

  // 스캔 중지
  const stopScanning = () => {
    isScanning.value = false;
  };

  // QR 디코드 성공
  const onDecode = decodedString => {
    scannedText.value = decodedString;
    if (!scanHistory.value.includes(decodedString)) {
      scanHistory.value.unshift(decodedString);
      if (scanHistory.value.length > 10) {
        scanHistory.value = scanHistory.value.slice(0, 10);
      }
    }
    stopScanning();
  };

  // QR 리더 초기화
  const onInit = promise => {
    promise
      .then(() => {
        console.log('QR 리더 초기화 성공');
      })
      .catch(error => {
        console.error('QR 리더 초기화 실패:', error);
        showAlert('오류', '카메라 접근 권한이 필요합니다.');
        stopScanning();
      });
  };

  // 스캔된 텍스트 복사
  const copyScannedText = async () => {
    try {
      await navigator.clipboard.writeText(scannedText.value);
      showAlert('복사 완료', '스캔된 텍스트가 클립보드에 복사되었습니다.');
    } catch (error) {
      console.error('복사 오류:', error);
      showAlert('오류', '복사 중 오류가 발생했습니다.');
    }
  };

  // 히스토리 텍스트 복사
  const copyHistoryText = async text => {
    try {
      await navigator.clipboard.writeText(text);
      showAlert('복사 완료', '텍스트가 클립보드에 복사되었습니다.');
    } catch (error) {
      console.error('복사 오류:', error);
      showAlert('오류', '복사 중 오류가 발생했습니다.');
    }
  };

  // 스캔된 텍스트 지우기
  const clearScannedText = () => {
    scannedText.value = '';
  };

  // 히스토리 지우기
  const clearHistory = () => {
    scanHistory.value = [];
  };

  // 알림 표시
  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  // 모달 확인
  const handleModalConfirm = () => {
    showModal.value = false;
  };

  // 모달 취소
  const handleModalCancel = () => {
    showModal.value = false;
  };

  // 컴포넌트 마운트 시 초기 QR 생성
  onMounted(() => {
    generateQR();
  });
</script>

<style scoped>
  /* QR 스캔 영역 스타일링 */
  .qr-scanner {
    position: relative;
    overflow: hidden;
    border-radius: 0.5rem;
  }

  /* 스캔 가이드 라인 */
  .scan-guide {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 200px;
    height: 200px;
    border: 2px solid #10b981;
    border-radius: 0.5rem;
    pointer-events: none;
  }

  .scan-guide::before,
  .scan-guide::after {
    content: '';
    position: absolute;
    width: 20px;
    height: 20px;
    border: 3px solid #10b981;
  }

  .scan-guide::before {
    top: -3px;
    left: -3px;
    border-right: none;
    border-bottom: none;
  }

  .scan-guide::after {
    bottom: -3px;
    right: -3px;
    border-left: none;
    border-top: none;
  }
</style>
