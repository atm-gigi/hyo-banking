<script setup>
  import { ref, onMounted, nextTick } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { formatDate } from '@/utils/formatters';
  import { getBankNameByCode } from '@/constants';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import Modal from '@/components/Modal.vue';
  import {
    getMacro,
    getMacroSteps,
    createQrToken,
    deleteMacro,
    deleteMacroStep,
    upsertMacroStep,
    updateMacro,
    getUserByAccount,
  } from '@/apis';

  const router = useRouter();
  const route = useRoute();
  const macro = ref(null);
  const macroSteps = ref([]);
  const isLoading = ref(false);
  const showDeleteModal = ref(false);
  const showStepDeleteModal = ref(false);
  const stepToDelete = ref(null);

  // 계좌 소유자 정보 캐시
  const accountOwners = ref(new Map());

  // 계좌 소유자 정보 가져오기
  const getAccountOwner = async (accountNo, bankCode) => {
    const key = `${accountNo}-${bankCode}`;

    if (accountOwners.value.has(key)) {
      return accountOwners.value.get(key);
    }

    try {
      const user = await getUserByAccount(accountNo, bankCode);
      accountOwners.value.set(key, user.name);
      return user.name;
    } catch (error) {
      console.error('계좌 소유자 조회 실패:', error);
      accountOwners.value.set(key, '알 수 없음');
      return '알 수 없음';
    }
  };

  // 계좌 소유자 정보를 가져오는 함수 (템플릿에서 사용)
  const getAccountOwnerName = (accountNo, bankCode) => {
    const key = `${accountNo}-${bankCode}`;
    return accountOwners.value.get(key) || '조회 중...';
  };

  // 매크로 이름 편집 관련
  const isEditingName = ref(false);
  const editingName = ref('');

  // 매크로 삭제 모달 설정
  const deleteModalConfig = ref({
    title: '',
    message: '',
    showCancel: true,
  });

  // 단계 삭제 모달 설정
  const stepDeleteModalConfig = ref({
    title: '',
    message: '',
    showCancel: true,
  });

  // 이름 변경 모달 설정
  const nameChangeModalConfig = ref({
    title: '',
    message: '',
    showCancel: true,
  });
  const showNameChangeModal = ref(false);

  onMounted(() => {
    loadMacro();
  });

  const loadMacro = async () => {
    const macroId = route.params.id;
    if (!macroId) return;

    try {
      isLoading.value = true;
      const [macroData, stepsData] = await Promise.all([getMacro(macroId), getMacroSteps(macroId)]);
      macro.value = macroData;
      macroSteps.value = stepsData;

      // 계좌 소유자 정보 가져오기
      const accountPromises = [];
      stepsData.forEach(step => {
        if (step.sourceAccountNo && step.sourceBankCode) {
          accountPromises.push(getAccountOwner(step.sourceAccountNo, step.sourceBankCode));
        }
        if (step.targetAccountNo && step.targetBankCode) {
          accountPromises.push(getAccountOwner(step.targetAccountNo, step.targetBankCode));
        }
      });

      await Promise.all(accountPromises);
    } catch (error) {
      console.error('간편 거래 로드 오류:', error);
    } finally {
      isLoading.value = false;
    }
  };

  const handleDeleteClick = () => {
    deleteModalConfig.value = {
      title: '간편 거래 삭제',
      message: '정말로 이 간편 거래를 삭제하시겠습니까?',
      showCancel: true,
    };
    showDeleteModal.value = true;
  };

  const handleDeleteConfirm = async () => {
    if (!macro.value) return;

    try {
      await deleteMacro(macro.value.id);
      showDeleteModal.value = false;
      router.push({ name: 'home' });
    } catch (error) {
      console.error('간편 거래 삭제 오류:', error);
      showDeleteModal.value = false;
    }
  };

  // 작업 추가 페이지로 이동
  const handleAddStepClick = () => {
    router.push({
      name: 'add-step',
      params: { id: macro.value.id },
    });
  };

  const handleDeleteCancel = () => {
    showDeleteModal.value = false;
  };

  // 단계 삭제 클릭
  const handleStepDeleteClick = step => {
    stepToDelete.value = step;
    stepDeleteModalConfig.value = {
      title: '단계 삭제',
      message: `"${getStepTypeText(step.stepType)}" 단계를 삭제하시겠습니까?`,
      showCancel: true,
    };
    showStepDeleteModal.value = true;
  };

  // 단계 삭제 확인
  const handleStepDeleteConfirm = async () => {
    if (!stepToDelete.value || !macro.value) return;

    try {
      await deleteMacroStep(macro.value.id, stepToDelete.value.stepOrder);
      showStepDeleteModal.value = false;
      stepToDelete.value = null;

      // 매크로 단계 다시 로드
      await loadMacro();

      // 삭제 후 단계 순서 재정렬
      await reorderSteps();
    } catch (error) {
      console.error('단계 삭제 오류:', error);
      alert('단계 삭제 중 오류가 발생했습니다.');
      showStepDeleteModal.value = false;
    }
  };

  // 단계 순서 재정렬
  const reorderSteps = async () => {
    if (!macro.value || macroSteps.value.length === 0) return;

    try {
      // 단계들을 stepOrder 순으로 정렬
      const sortedSteps = [...macroSteps.value].sort((a, b) => a.stepOrder - b.stepOrder);

      // 1부터 시작하는 연속된 순서로 재정렬
      for (let i = 0; i < sortedSteps.length; i++) {
        const newOrder = i + 1;
        if (sortedSteps[i].stepOrder !== newOrder) {
          await upsertMacroStep(macro.value.id, {
            stepId: sortedSteps[i].id,
            stepOrder: newOrder,
            stepType: sortedSteps[i].stepType,
            amount: sortedSteps[i].amount,
            currencyCode: sortedSteps[i].currencyCode,
            sourceAccountNo: sortedSteps[i].sourceAccountNo,
            sourceBankCode: sortedSteps[i].sourceBankCode,
            targetAccountNo: sortedSteps[i].targetAccountNo,
            targetBankCode: sortedSteps[i].targetBankCode,
            note: sortedSteps[i].note,
          });
        }
      }

      // 매크로 단계 다시 로드
      await loadMacro();
    } catch (error) {
      console.error('단계 순서 재정렬 오류:', error);
    }
  };

  // 단계 삭제 취소
  const handleStepDeleteCancel = () => {
    showStepDeleteModal.value = false;
    stepToDelete.value = null;
  };

  // 매크로 이름 편집 시작
  const startEditName = () => {
    if (!macro.value) return;
    editingName.value = macro.value.name;
    isEditingName.value = true;

    // 다음 틱에서 입력 필드에 포커스
    nextTick(() => {
      const nameInput = document.querySelector('input[ref="nameInput"]');
      if (nameInput) {
        nameInput.focus();
        nameInput.select();
      }
    });
  };

  // 매크로 이름 편집 취소
  const cancelEditName = () => {
    isEditingName.value = false;
    editingName.value = '';
  };

  // 매크로 이름 저장
  const saveMacroName = async () => {
    if (!macro.value || !editingName.value.trim()) {
      nameChangeModalConfig.value = {
        title: '오류',
        message: '간편 거래 이름을 입력해주세요.',
        showCancel: false,
      };
      showNameChangeModal.value = true;
      return;
    }

    try {
      await updateMacro(macro.value.id, editingName.value.trim(), macro.value.status);

      // 매크로 정보 다시 로드
      await loadMacro();

      isEditingName.value = false;
      editingName.value = '';

      nameChangeModalConfig.value = {
        title: '수정 완료',
        message: '간편 거래 이름이 변경되었습니다.',
        showCancel: false,
      };
      showNameChangeModal.value = true;
    } catch (error) {
      console.error('간편 거래 이름 수정 오류:', error);
      nameChangeModalConfig.value = {
        title: '오류',
        message: '간편 거래 이름 수정 중 오류가 발생했습니다.',
        showCancel: false,
      };
      showNameChangeModal.value = true;
    }
  };

  // 이름 변경 모달 확인
  const handleNameChangeConfirm = () => {
    showNameChangeModal.value = false;
  };

  const handleQrClick = async () => {
    if (!macro.value) return;

    try {
      const qrToken = await createQrToken(macro.value.id);
      router.push({
        name: 'show-qr',
        params: { id: macro.value.id },
        query: {
          token: qrToken.token,
          macroName: macro.value.name,
        },
      });
    } catch (error) {
      console.error('QR 토큰 생성 오류:', error);
      alert('QR 코드 생성 중 오류가 발생했습니다.');
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
        return '송금';
      default:
        return '거래';
    }
  };

  const getStepTypeIcon = type => {
    switch (type) {
      case 'BALANCE_CHECK':
        return '💰';
      case 'WITHDRAW':
        return '💸';
      case 'DEPOSIT':
        return '📥';
      case 'TRANSFER':
        return '📤';
      default:
        return '💳';
    }
  };

  const formatAmount = amount => {
    if (!amount) return '0원';
    return `${(amount / 10000).toLocaleString()}만원`;
  };
</script>

<template>
  <main class="w-full h-full flex flex-col">
    <!-- 헤더 -->
    <div class="px-5 pt-10 pb-6">
      <div class="mb-6">
        <BackButton :handleGoBack="() => router.push({ name: 'home' })" />
      </div>
      <h1 class="text-2xl font-bold">간편 거래 상세</h1>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="px-5 flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-gray-500 text-lg">간편 거래 정보를 불러오는 중...</p>
      </div>
    </div>

    <!-- 매크로 상세 정보 -->
    <div v-else-if="macro" class="px-5 flex-1">
      <!-- 매크로 기본 정보 -->
      <div class="bg-white rounded-2xl shadow-sm p-6 mb-6 relative">
        <!-- 이름 변경 버튼 -->
        <button
          v-if="!isEditingName"
          @click="startEditName"
          class="absolute top-4 right-4 text-blue-600 hover:text-blue-800 transition-colors text-sm font-medium"
        >
          이름 변경
        </button>

        <div class="flex items-center justify-center my-4">
          <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mr-4">
            <span class="text-blue-600 font-bold text-xl">📱</span>
          </div>
          <div class="text-center flex-1">
            <!-- 매크로 이름 편집 모드 -->
            <div v-if="isEditingName" class="flex items-center justify-center gap-2 mb-2">
              <input
                v-model="editingName"
                type="text"
                class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-center"
                placeholder="간편 거래 이름을 입력하세요"
                @keyup.enter="saveMacroName"
                @keyup.escape="cancelEditName"
                ref="nameInput"
              />
              <button
                @click="saveMacroName"
                class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm"
                title="저장"
              >
                ✓
              </button>
              <button
                @click="cancelEditName"
                class="px-3 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors text-sm"
                title="취소"
              >
                ✕
              </button>
            </div>
            <!-- 매크로 이름 표시 모드 -->
            <div v-else class="flex items-center justify-center mb-2">
              <h2 class="text-xl font-bold text-gray-900">{{ macro.name }}</h2>
            </div>
            <p class="text-sm text-gray-500">{{ formatDate(macro.createdAt) }}</p>
          </div>
        </div>
      </div>

      <!-- 매크로 단계들 -->
      <div class="bg-white rounded-2xl shadow-sm p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-700 mb-4">실행 단계</h3>
        <div v-if="macroSteps.length === 0" class="text-center py-4 text-gray-500">
          <p>등록된 단계가 없습니다.</p>
        </div>
        <div v-else class="space-y-3">
          <div
            v-for="(step, index) in macroSteps"
            :key="step.id"
            class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg group hover:bg-gray-100 transition-colors"
          >
            <div
              class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-bold text-sm"
            >
              {{ index + 1 }}
            </div>
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <span class="text-lg">{{ getStepTypeIcon(step.stepType) }}</span>
                <span class="font-medium text-gray-900">{{ getStepTypeText(step.stepType) }}</span>
              </div>
              <div v-if="step.amount" class="text-sm text-gray-600">
                금액: {{ formatAmount(step.amount) }}
              </div>
              <div v-if="step.sourceAccountNo" class="text-sm text-gray-500">
                <div>{{ step.sourceAccountNo }}</div>
                <div>국민은행</div>
                <div>{{ getAccountOwnerName(step.sourceAccountNo, step.sourceBankCode) }}</div>
              </div>
              <div v-if="step.targetAccountNo" class="text-sm text-gray-500">
                <div>{{ step.targetAccountNo }}</div>
                <div>국민은행</div>
                <div>{{ getAccountOwnerName(step.targetAccountNo, step.targetBankCode) }}</div>
              </div>
            </div>
            <button
              @click="handleStepDeleteClick(step)"
              class="w-6 h-6 bg-red-100 text-red-600 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-200"
              title="단계 삭제"
            >
              ×
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 매크로를 찾을 수 없는 경우 -->
    <div v-else class="px-5 flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-gray-500 text-lg">간편 거래 정보를 찾을 수 없습니다.</p>
        <button
          @click="() => router.push({ name: 'home' })"
          class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-xl font-semibold"
        >
          홈으로 돌아가기
        </button>
      </div>
    </div>

    <div v-if="macro" class="px-5 flex gap-5 pb-10">
      <button
        @click="handleDeleteClick"
        class="w-full py-2 bg-red-500 text-md font-bold text-red-50 rounded-2xl hover:scale-105 active:scale-95 active:brightness-75 transition cursor-pointer"
      >
        삭제
      </button>
      <button
        @click="handleAddStepClick"
        class="w-full py-2 bg-green-500 text-md font-bold text-green-50 rounded-2xl hover:scale-105 active:scale-95 active:brightness-75 transition cursor-pointer"
      >
        작업 추가
      </button>
      <PrimaryBtn class="w-full py-2" text="QR 만들기" @click="handleQrClick" />
    </div>

    <!-- 매크로 삭제 확인 모달 -->
    <Modal
      :isVisible="showDeleteModal"
      :title="deleteModalConfig.title"
      :message="deleteModalConfig.message"
      :showCancel="deleteModalConfig.showCancel"
      @confirm="handleDeleteConfirm"
      @cancel="handleDeleteCancel"
    />

    <!-- 단계 삭제 확인 모달 -->
    <Modal
      :isVisible="showStepDeleteModal"
      :title="stepDeleteModalConfig.title"
      :message="stepDeleteModalConfig.message"
      :showCancel="stepDeleteModalConfig.showCancel"
      @confirm="handleStepDeleteConfirm"
      @cancel="handleStepDeleteCancel"
    />

    <!-- 이름 변경 결과 모달 -->
    <Modal
      :isVisible="showNameChangeModal"
      :title="nameChangeModalConfig.title"
      :message="nameChangeModalConfig.message"
      :showCancel="nameChangeModalConfig.showCancel"
      @confirm="handleNameChangeConfirm"
    />
  </main>
</template>
