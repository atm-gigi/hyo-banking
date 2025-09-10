<script setup>
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  import BackButton from '@/components/buttons/GoBackBtn.vue';
  import JoinStep1 from '@/components/JoinStep1.vue';
  import JoinStep2 from '@/components/JoinStep2.vue';
  import Modal from '@/components/Modal.vue';

  const router = useRouter();
  const authStore = useAuthStore();

  // 반응형 데이터
  const isLoading = ref(false);
  const currentStep = ref(1); // 1: 이름/전화번호, 2: 아이디/비밀번호
  const joinData = reactive({
    loginId: '',
    password: '',
    confirmPassword: '',
    name: '',
    phone: '',
  });
  const joinMessage = ref(null);
  const showModal = ref(false);
  const modalConfig = ref({
    title: '',
    message: '',
    showCancel: false,
  });

  // 2단계 처리 (아이디, 비밀번호)
  const handleStep2 = async () => {
    isLoading.value = true;
    joinMessage.value = null;

    try {
      // 회원가입 요청
      const result = await authStore.join(joinData);

      if (result.success) {
        // 회원가입 성공 시 자동 로그인
        const loginResult = await authStore.login({
          loginId: joinData.loginId,
          password: joinData.password,
        });

        if (loginResult.success) {
          showAlert('회원가입 완료', '회원가입 및 로그인이 완료되었습니다!');
        } else {
          showAlert(
            '회원가입 완료',
            '회원가입은 완료되었지만 로그인에 실패했습니다. 다시 로그인해주세요.'
          );
        }
      } else {
        joinMessage.value = {
          type: 'error',
          text: result.message,
        };
      }
    } catch (error) {
      console.error('Join error:', error);
      joinMessage.value = {
        type: 'error',
        text: '회원가입 중 오류가 발생했습니다.',
      };
    } finally {
      isLoading.value = false;
    }
  };

  // 다음 단계로 이동
  const handleNext = () => {
    currentStep.value = 2;
  };

  // 이전 단계로 이동
  const handleBack = () => {
    currentStep.value = 1;
    joinMessage.value = null;
  };

  // 폼 데이터 업데이트
  const updateFormData = newData => {
    Object.assign(joinData, newData);
  };

  // 모달 표시
  const showAlert = (title, message, showCancel = false) => {
    modalConfig.value = { title, message, showCancel };
    showModal.value = true;
  };

  // 모달 확인
  const handleModalConfirm = () => {
    showModal.value = false;
    router.push('/');
  };

  // 모달 취소
  const handleModalCancel = () => {
    showModal.value = false;
  };
</script>

<template>
  <main class="w-full h-full flex flex-col justify-between">
    <div class="px-5 h-full flex flex-col">
      <div class="pt-10 pb-8">
        <BackButton :handle-go-back="currentStep == 2 ? handleBack : null" />
      </div>

      <h1 class="text-2xl font-bold mb-10">
        {{ currentStep === 1 ? '회원가입 (1/2)' : '회원가입 (2/2)' }}
      </h1>

      <!-- 1단계: 이름, 전화번호 -->
      <JoinStep1
        v-if="currentStep === 1"
        :form-data="joinData"
        :is-loading="isLoading"
        @next="handleNext"
        @update:form-data="updateFormData"
        class="flex-1"
      />

      <!-- 2단계: 아이디, 비밀번호 -->
      <JoinStep2
        v-if="currentStep === 2"
        :form-data="joinData"
        :is-loading="isLoading"
        @join="handleStep2"
        @back="handleBack"
        @update:form-data="updateFormData"
        class="flex-1"
      />
    </div>

    <!-- 메시지 표시 -->
    <div v-if="joinMessage" class="px-5">
      <div
        :class="[
          'text-center text-sm p-3 rounded-lg whitespace-pre-line',
          joinMessage.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700',
        ]"
      >
        {{ joinMessage.text }}
      </div>
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
