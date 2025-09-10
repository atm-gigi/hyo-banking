<script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { formatDate } from '@/utils/formatters';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';
  import { getMacro, getMacroSteps, getMacros, addMacroStep } from '@/apis';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';

  const router = useRouter();
  const route = useRoute();

  const currentMacro = ref(null);
  const availableMacros = ref([]);
  const selectedMacro = ref(null);
  const selectedMacroSteps = ref([]);
  const isLoading = ref(false);
  const showModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  onMounted(() => {
    loadData();
  });

  const loadData = async () => {
    const macroId = route.params.id;
    if (!macroId) {
      router.push({ name: 'home' });
      return;
    }

    try {
      isLoading.value = true;

      // 현재 매크로 로드
      currentMacro.value = await getMacro(macroId);

      // 사용자의 다른 매크로들 로드
      const response = await getMacros(currentMacro.value.userId, null, 0, 100);
      availableMacros.value = response.content.filter(m => m.id !== macroId);
    } catch (error) {
      console.error('데이터 로드 오류:', error);
      showAlert('오류', '데이터를 불러오는 중 오류가 발생했습니다.');
    } finally {
      isLoading.value = false;
    }
  };

  const selectMacro = async macro => {
    selectedMacro.value = macro;

    try {
      // 선택된 매크로의 단계들 로드
      selectedMacroSteps.value = await getMacroSteps(macro.id);
    } catch (error) {
      console.error('매크로 단계 로드 오류:', error);
      selectedMacroSteps.value = [];
    }
  };

  const handleAddStep = async () => {
    if (!selectedMacro.value) return;

    try {
      // 현재 매크로의 기존 단계 수를 가져와서 순서 설정
      const currentMacroSteps = await getMacroSteps(currentMacro.value.id);
      let nextStepOrder = currentMacroSteps.length + 1;

      // 선택된 매크로의 단계들을 현재 매크로에 추가
      for (const step of selectedMacroSteps.value) {
        const newStepData = {
          stepOrder: nextStepOrder,
          stepType: step.stepType,
          amount: step.amount,
          currencyCode: step.currencyCode,
          sourceAccountNo: step.sourceAccountNo,
          sourceBankCode: step.sourceBankCode,
          targetAccountNo: step.targetAccountNo,
          targetBankCode: step.targetBankCode,
          note: step.note,
        };
        await addMacroStep(currentMacro.value.id, newStepData);
        nextStepOrder++; // 다음 단계 순서 증가
      }

      showAlert('작업 추가 완료', `${selectedMacro.value.name}의 작업이 추가되었습니다.`);
    } catch (error) {
      console.error('작업 추가 오류:', error);
      showAlert('오류', '작업 추가 중 오류가 발생했습니다.');
    }
  };

  const getStepTypeText = type => {
    switch (type) {
      case 'BALANCE_CHECK':
        return '잔액 조회';
      case 'WITHDRAW':
        return '출금';
      case 'DEPOSIT':
        return '입금';
      case 'TRANSFER':
        return '이체';
      default:
        return '거래';
    }
  };

  const formatAmount = amount => {
    if (!amount) return '0원';
    return `${(amount / 10000).toLocaleString()}만원`;
  };

  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  const handleModalConfirm = () => {
    showModal.value = false;
    router.push({
      name: 'transaction-detail',
      params: { id: currentMacro.value.id },
    });
  };

  const handleModalCancel = () => {
    showModal.value = false;
  };
</script>

<template>
  <main class="w-full h-full flex flex-col">
    <div class="px-5 pt-10 pb-6">
      <div class="mb-6">
        <BackButton />
      </div>

      <h1 class="text-2xl font-bold">작업 추가</h1>
    </div>

    <div v-if="isLoading" class="px-5 flex-1 flex items-center justify-center">
      <div class="text-center">
        <div
          class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600 mx-auto mb-4"
        ></div>
        <p class="text-gray-500 text-lg">매크로 목록을 불러오는 중...</p>
      </div>
    </div>

    <div
      v-else-if="availableMacros.length === 0"
      class="px-5 flex-1 flex items-center justify-center"
    >
      <div class="text-center">
        <div class="text-gray-400 text-6xl mb-4">📱</div>
        <p class="text-gray-500 text-lg mb-2">추가할 수 있는 매크로가 없습니다</p>
        <p class="text-gray-400 text-sm">다른 매크로를 먼저 생성해주세요</p>
        <button
          @click="() => router.push({ name: 'home' })"
          class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-xl font-semibold"
        >
          홈으로 돌아가기
        </button>
      </div>
    </div>

    <div v-else class="px-5 flex-1">
      <div class="mb-6">
        <h2 class="text-lg font-semibold text-gray-700 mb-2">현재 매크로</h2>
        <div class="bg-blue-50 rounded-lg p-4">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
              <span class="text-blue-600 font-bold text-lg">📱</span>
            </div>
            <div>
              <p class="font-medium text-gray-900">{{ currentMacro.name }}</p>
              <p class="text-sm text-gray-500">{{ formatDate(currentMacro.createdAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="mb-6">
        <h2 class="text-lg font-semibold text-gray-700 mb-4">추가할 매크로 선택</h2>
        <div class="space-y-3">
          <div
            v-for="availableMacro in availableMacros"
            :key="availableMacro.id"
            @click="selectMacro(availableMacro)"
            class="flex items-center p-4 rounded-lg cursor-pointer transition-colors border-2"
            :class="{
              'bg-blue-50 border-blue-300': selectedMacro?.id === availableMacro.id,
              'bg-white border-gray-200 hover:bg-gray-50': selectedMacro?.id !== availableMacro.id,
            }"
          >
            <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mr-4">
              <span class="text-green-600 font-bold text-xl">➕</span>
            </div>
            <div class="flex-1">
              <p class="font-medium text-gray-900">{{ availableMacro.name }}</p>
              <p class="text-sm text-gray-500">{{ formatDate(availableMacro.createdAt) }}</p>
              <div class="mt-1">
                <span
                  class="text-xs px-2 py-1 rounded-full"
                  :class="{
                    'bg-green-100 text-green-700': availableMacro.status === 'ACTIVE',
                    'bg-yellow-100 text-yellow-700': availableMacro.status === 'DRAFT',
                    'bg-gray-100 text-gray-700': availableMacro.status === 'INACTIVE',
                  }"
                >
                  {{
                    availableMacro.status === 'DRAFT'
                      ? '작성중'
                      : availableMacro.status === 'ACTIVE'
                        ? '활성'
                        : '비활성'
                  }}
                </span>
              </div>
            </div>
            <div v-if="selectedMacro?.id === availableMacro.id" class="text-blue-600 text-xl">
              ✓
            </div>
          </div>
        </div>
      </div>

      <div v-if="selectedMacro" class="mb-6">
        <h3 class="text-lg font-semibold text-gray-700 mb-4">선택된 매크로 상세</h3>
        <div class="bg-gray-50 rounded-lg p-4">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
              <span class="text-green-600 font-bold text-lg">📱</span>
            </div>
            <div>
              <p class="font-medium text-gray-900">{{ selectedMacro.name }}</p>
              <p class="text-sm text-gray-500">{{ formatDate(selectedMacro.createdAt) }}</p>
            </div>
          </div>

          <div v-if="selectedMacroSteps.length > 0">
            <p class="text-sm font-medium text-gray-700 mb-2">포함된 작업:</p>
            <div class="space-y-2">
              <div
                v-for="(step, index) in selectedMacroSteps"
                :key="step.id"
                class="flex items-center gap-2 p-2 bg-white rounded border"
              >
                <div
                  class="w-6 h-6 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-bold text-xs"
                >
                  {{ index + 1 }}
                </div>
                <span class="text-sm text-gray-700">{{ getStepTypeText(step.stepType) }}</span>
                <span v-if="step.amount" class="text-sm text-gray-500">
                  - {{ formatAmount(step.amount) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="availableMacros.length > 0" class="px-5 pb-10">
      <div class="flex gap-3">
        <button
          @click="() => router.back()"
          class="w-full py-2 bg-gray-200 text-gray-700 rounded-xl font-medium"
        >
          취소
        </button>
        <PrimaryBtn
          class="w-full py-2"
          @click="handleAddStep"
          :disabled="!selectedMacro"
          text="작업 추가"
        />
      </div>
    </div>

    <!-- 확인 모달 -->
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
