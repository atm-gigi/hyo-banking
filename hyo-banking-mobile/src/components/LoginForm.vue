<script setup>
  import { ref, computed, reactive } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '../stores/auth';
  import { validateLoginForm, createLoginAttemptManager } from '../utils/auth';

  const router = useRouter();
  const authStore = useAuthStore();
  const loginAttemptManager = createLoginAttemptManager();

  // 반응형 데이터
  const isLoading = ref(false);
  const credentials = reactive({
    username: '',
    password: '',
    rememberMe: false,
  });
  const formErrors = reactive({});
  const loginMessage = ref(null);

  // 계산된 속성
  const isFormValid = computed(() => {
    const validation = validateLoginForm(credentials);
    return validation.isValid;
  });

  const lockoutTimeRemaining = ref(0);

  // 메서드
  const handleLogin = async () => {
    // 폼 유효성 검사
    const validation = validateLoginForm(credentials);
    if (!validation.isValid) {
      Object.assign(formErrors, validation.errors);
      return;
    }

    // 로그인 시도 제한 확인
    if (loginAttemptManager.isLockedOut()) {
      lockoutTimeRemaining.value = loginAttemptManager.getLockoutTimeRemaining();
      return;
    }

    // 폼 에러 초기화
    Object.keys(formErrors).forEach(key => delete formErrors[key]);
    loginMessage.value = null;

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
        text: '로그인 중 오류가 발생했습니다.',
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
  <div
    class="min-h-screen bg-gradient-to-br from-kb-yellow-100 to-kb-yellow-200 flex items-center justify-center p-4"
  >
    <div class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-kb-brown-200 mb-2">KB 효뱅킹</h1>
        <p class="text-kb-gray-100">더 쉬운 은행 서비스</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label for="username" class="block text-sm font-medium text-kb-brown-200 mb-2">
            아이디
          </label>
          <input
            id="username"
            v-model="credentials.username"
            type="text"
            :class="[
              'w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-kb-yellow-200 focus:border-kb-yellow-200 outline-none transition-colors',
              formErrors.username ? 'border-red-500' : 'border-gray-300',
            ]"
            placeholder="아이디를 입력하세요"
            :disabled="isLoading"
          />
          <p v-if="formErrors.username" class="text-red-500 text-sm mt-1">
            {{ formErrors.username }}
          </p>
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-kb-brown-200 mb-2">
            비밀번호
          </label>
          <input
            id="password"
            v-model="credentials.password"
            type="password"
            :class="[
              'w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-kb-yellow-200 focus:border-kb-yellow-200 outline-none transition-colors',
              formErrors.password ? 'border-red-500' : 'border-gray-300',
            ]"
            placeholder="비밀번호를 입력하세요"
            :disabled="isLoading"
          />
          <p v-if="formErrors.password" class="text-red-500 text-sm mt-1">
            {{ formErrors.password }}
          </p>
        </div>

        <div class="flex items-center justify-between">
          <label class="flex items-center">
            <input
              v-model="credentials.rememberMe"
              type="checkbox"
              class="w-4 h-4 text-kb-yellow-200 border-gray-300 rounded focus:ring-kb-yellow-200"
              :disabled="isLoading"
            />
            <span class="ml-2 text-sm text-kb-gray-100">로그인 상태 유지</span>
          </label>
        </div>

        <button
          type="submit"
          :disabled="isLoading || !isFormValid"
          :class="[
            'w-full py-3 px-4 rounded-lg font-semibold text-white transition-all duration-300',
            isLoading || !isFormValid
              ? 'bg-gray-400 cursor-not-allowed'
              : 'bg-kb-brown-200 hover:bg-kb-brown-100 active:scale-105',
          ]"
        >
          <span v-if="isLoading" class="flex items-center justify-center">
            <svg
              class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
            로그인 중...
          </span>
          <span v-else>로그인</span>
        </button>

        <div
          v-if="loginMessage"
          :class="[
            'text-center text-sm p-3 rounded-lg',
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

      <div class="mt-6 text-center">
        <p class="text-sm text-kb-gray-100">
          테스트 계정: <span class="font-mono">test / 1234</span>
        </p>
      </div>
    </div>
  </div>
</template>
