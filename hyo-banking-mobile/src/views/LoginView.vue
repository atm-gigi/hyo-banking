<script setup>
  import { ref, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '@/stores/auth';
  import { validateLoginForm, createLoginAttemptManager } from '@/utils/auth';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import TextInput from '@/components/TextInput.vue';

  const router = useRouter();
  const authStore = useAuthStore();
  const loginAttemptManager = createLoginAttemptManager();

  // 반응형 데이터
  const isLoading = ref(false);
  const credentials = reactive({
    loginId: '',
    password: '',
    rememberMe: false,
  });
  const formErrors = reactive({});
  const loginMessage = ref(null);

  // 계산된 속성

  const lockoutTimeRemaining = ref(0);

  // 메서드
  const handleLogin = async () => {
    // 폼 에러 초기화
    Object.keys(formErrors).forEach(key => delete formErrors[key]);
    loginMessage.value = null;

    // 단계별 검증
    const validationErrors = [];

    // 1. 로그인 시도 제한 확인
    if (loginAttemptManager.isLockedOut()) {
      lockoutTimeRemaining.value = loginAttemptManager.getLockoutTimeRemaining();
      validationErrors.push('로그인 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.');
    }

    // 2. 폼 유효성 검사
    const validation = validateLoginForm(credentials);
    if (!validation.isValid) {
      Object.assign(formErrors, validation.errors);

      // 구체적인 필드별 에러 메시지 추가
      if (validation.errors.loginId) validationErrors.push('아이디: ' + validation.errors.loginId);
      if (validation.errors.password)
        validationErrors.push('비밀번호: ' + validation.errors.password);
    }

    // 검증 실패 시 에러 메시지 표시
    if (validationErrors.length > 0) {
      loginMessage.value = {
        type: 'error',
        text: validationErrors.join('\n'),
      };
      return;
    }

    isLoading.value = true;

    try {
      const result = await authStore.login(credentials);

      if (result.success) {
        loginMessage.value = {
          type: 'success',
          text: '로그인에 성공했습니다!',
        };

        // 성공 시 시도 횟수 초기화
        loginAttemptManager.recordAttempt(true);

        // 홈으로 이동
        router.push('/');
      } else {
        loginMessage.value = {
          type: 'error',
          text: result.message,
        };

        // 실패 시 시도 횟수 기록
        const attemptResult = loginAttemptManager.recordAttempt(false);
        if (!attemptResult.canAttempt) {
          lockoutTimeRemaining.value = loginAttemptManager.getLockoutTimeRemaining();
        }
      }
    } catch (error) {
      console.error('Login error:', error);
      loginMessage.value = {
        type: 'error',
        text: error.message || '로그인 중 오류가 발생했습니다.',
      };
    } finally {
      isLoading.value = false;
    }
  };

  const formatTime = seconds => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}분 ${remainingSeconds}초`;
  };

  // 컴포넌트 마운트 시 잠금 상태 확인
  const checkLockoutStatus = () => {
    if (loginAttemptManager.isLockedOut()) {
      lockoutTimeRemaining.value = loginAttemptManager.getLockoutTimeRemaining();

      // 잠금 해제까지 남은 시간 업데이트
      const interval = setInterval(() => {
        lockoutTimeRemaining.value = loginAttemptManager.getLockoutTimeRemaining();
        if (lockoutTimeRemaining.value <= 0) {
          clearInterval(interval);
        }
      }, 1000);
    }
  };

  // 컴포넌트 마운트 시 실행
  checkLockoutStatus();
</script>

<template>
  <main class="h-full w-full bg-white flex flex-col justify-between">
    <div class="text-center py-20">
      <h1 class="text-3xl font-bold text-kb-brown-200 mb-2">KB 효뱅킹</h1>
      <p class="text-kb-gray-100">더 쉬운 ATM 서비스</p>
    </div>

    <div class="h-full px-5">
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div class="mb-10">
          <TextInput
            id="loginId"
            v-model="credentials.loginId"
            type="text"
            name="loginId"
            text="아이디"
            placeholder="아이디를 입력하세요"
            :required="true"
            :readonly="isLoading"
            :class="formErrors.loginId ? 'border-red-500' : ''"
          />
          <p v-if="formErrors.loginId" class="text-red-500 text-sm mt-1">
            {{ formErrors.loginId }}
          </p>
        </div>

        <div>
          <TextInput
            id="password"
            v-model="credentials.password"
            type="password"
            name="password"
            text="비밀번호"
            placeholder="비밀번호를 입력하세요"
            :required="true"
            :readonly="isLoading"
            :class="formErrors.password ? 'border-red-500' : ''"
          />
          <p v-if="formErrors.password" class="text-red-500 text-sm mt-1">
            {{ formErrors.password }}
          </p>
        </div>

        <div class="mt-6 text-center">
          <p class="text-sm text-kb-gray-100">
            테스트 계정: <span class="font-mono">test / 1234</span>
          </p>
        </div>

        <div
          v-if="loginMessage"
          :class="[
            'text-center text-sm p-3 rounded-lg whitespace-pre-line',
            loginMessage.type === 'error'
              ? 'bg-red-100 text-red-700'
              : 'bg-green-100 text-green-700',
          ]"
        >
          {{ loginMessage.text }}
        </div>

        <div
          v-if="lockoutTimeRemaining > 0"
          class="text-center text-sm text-red-600 bg-red-100 p-3 rounded-lg"
        >
          로그인 시도 횟수를 초과했습니다. {{ formatTime(lockoutTimeRemaining) }} 후에 다시
          시도해주세요.
        </div>
      </form>
    </div>

    <div class="px-5 pb-10 w-full">
      <PrimaryBtn
        text="로그인"
        :disabled="isLoading"
        :isLoading="isLoading"
        @click="handleLogin"
        class="w-full py-3"
      />
    </div>
  </main>
</template>
