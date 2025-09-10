<script setup lang="ts">
  import { ref, reactive } from 'vue';
  import TextInput from '@/components/TextInput.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import { checkDuplicateId } from '@/apis';

  const emit = defineEmits(['join', 'back', 'update:formData']);

  const props = defineProps({
    formData: {
      type: Object,
      required: true,
    },
    isLoading: {
      type: Boolean,
      default: false,
    },
  });

  // 반응형 데이터
  const formErrors = reactive({
    loginId: '',
    password: '',
    confirmPassword: '',
  });
  const message = ref(null);

  // 아이디 중복 체크 관련 상태
  const idCheck = reactive({
    isChecking: false,
    isChecked: false,
    isAvailable: false,
  });

  // 2단계 폼 유효성 검사 (아이디, 비밀번호)
  const validateStep2Form = data => {
    const errors = {};

    // 아이디 검증
    if (!data.loginId || data.loginId.trim() === '') {
      errors.loginId = '아이디를 입력해주세요.';
    } else if (data.loginId.length < 3) {
      errors.loginId = '아이디는 3자 이상 입력해주세요.';
    } else if (!/^[a-zA-Z0-9]+$/.test(data.loginId)) {
      errors.loginId = '아이디는 영문과 숫자만 사용 가능합니다.';
    }

    // 비밀번호 검증
    if (!data.password || data.password.trim() === '') {
      errors.password = '비밀번호를 입력해주세요.';
    } else if (data.password.length < 4) {
      errors.password = '비밀번호는 4자 이상 입력해주세요.';
    }

    // 비밀번호 확인 검증
    if (!data.confirmPassword || data.confirmPassword.trim() === '') {
      errors.confirmPassword = '비밀번호 확인을 입력해주세요.';
    } else if (data.password !== data.confirmPassword) {
      errors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors,
    };
  };

  // 아이디 중복 체크
  const handleCheckDuplicateId = async () => {
    if (!props.formData.loginId || props.formData.loginId.length < 3) {
      message.value = {
        type: 'error',
        text: '아이디를 3자 이상 입력해주세요.',
      };
      return;
    }

    idCheck.isChecking = true;
    message.value = null;

    try {
      const response = await checkDuplicateId(props.formData.loginId);

      if (response) {
        idCheck.isAvailable = false;
        idCheck.isChecked = true;
        message.value = {
          type: 'error',
          text: '이미 사용 중인 아이디입니다.',
        };
      } else {
        idCheck.isAvailable = true;
        idCheck.isChecked = true;
        message.value = {
          type: 'success',
          text: '사용 가능한 아이디입니다.',
        };
      }
    } catch (error) {
      idCheck.isAvailable = false;
      idCheck.isChecked = false;
      message.value = {
        type: 'error',
        text: error.message || '아이디 중복 체크 중 오류가 발생했습니다.',
      };
    } finally {
      idCheck.isChecking = false;
    }
  };

  // 2단계 처리 (아이디, 비밀번호)
  const handleStep2 = () => {
    // 폼 에러 초기화
    Object.keys(formErrors).forEach(key => delete formErrors[key]);
    message.value = null;

    // 2단계 폼 유효성 검사
    const validation = validateStep2Form(props.formData);
    if (!validation.isValid) {
      Object.assign(formErrors, validation.errors);

      const validationErrors = [];
      if (validation.errors.loginId) validationErrors.push('아이디: ' + validation.errors.loginId);
      if (validation.errors.password)
        validationErrors.push('비밀번호: ' + validation.errors.password);
      if (validation.errors.confirmPassword)
        validationErrors.push('비밀번호 확인: ' + validation.errors.confirmPassword);

      message.value = {
        type: 'error',
        text: validationErrors.join('\n'),
      };
      return;
    }

    // 아이디 중복 체크 검증
    if (!idCheck.isChecked) {
      message.value = {
        type: 'error',
        text: '아이디 중복 체크를 완료해주세요.',
      };
      return;
    } else if (!idCheck.isAvailable) {
      message.value = {
        type: 'error',
        text: '이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.',
      };
      return;
    }

    // 회원가입 처리
    emit('join');
  };
</script>

<template>
  <div class="flex flex-col justify-between">
    <div class="flex-1 space-y-6">
      <!-- 아이디 -->
      <div>
        <div class="flex gap-2">
          <div class="flex-1">
            <TextInput
              id="loginId"
              :model-value="formData.loginId"
              @update:model-value="
                value => emit('update:formData', { ...formData, loginId: value })
              "
              type="text"
              name="loginId"
              text="아이디"
              placeholder="아이디를 입력하세요"
              :required="true"
              :readonly="isLoading"
              :class="[
                formErrors.loginId ? 'border-red-500' : '',
                idCheck.isChecked && idCheck.isAvailable ? 'border-green-500' : '',
                idCheck.isChecked && !idCheck.isAvailable ? 'border-red-500' : '',
              ]"
              @input="
                () => {
                  idCheck.isChecked = false;
                  idCheck.isAvailable = false;
                }
              "
            />
          </div>
          <button
            v-if="!idCheck.isChecked || !idCheck.isAvailable"
            type="button"
            @click="handleCheckDuplicateId"
            :disabled="
              idCheck.isChecking ||
              !formData.loginId ||
              formData.loginId.length < 3 ||
              !/^[a-zA-Z0-9]+$/.test(formData.loginId)
            "
            :class="[
              'px-4 py-2 text-sm font-medium rounded-lg border transition-colors',
              idCheck.isChecking ||
              !formData.loginId ||
              formData.loginId.length < 3 ||
              !/^[a-zA-Z0-9]+$/.test(formData.loginId)
                ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                : 'bg-kb-yellow-200 text-kb-brown-200 border-kb-yellow-200 hover:bg-kb-yellow-300',
            ]"
          >
            <span v-if="idCheck.isChecking">확인중...</span>
            <span v-else>중복체크</span>
          </button>
          <div
            v-else
            class="flex items-center px-4 py-2 text-sm font-medium text-green-600 bg-green-100 rounded-lg border border-green-200"
          >
            <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                clip-rule="evenodd"
              />
            </svg>
            사용가능
          </div>
        </div>
        <p v-if="formErrors.loginId" class="text-red-500 text-sm mt-1">
          {{ formErrors.loginId }}
        </p>
      </div>

      <!-- 비밀번호 -->
      <div>
        <TextInput
          id="password"
          :model-value="formData.password"
          @update:model-value="value => emit('update:formData', { ...formData, password: value })"
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

      <!-- 비밀번호 확인 -->
      <div>
        <TextInput
          id="confirmPassword"
          :model-value="formData.confirmPassword"
          @update:model-value="
            value => emit('update:formData', { ...formData, confirmPassword: value })
          "
          type="password"
          name="confirmPassword"
          text="비밀번호 확인"
          placeholder="비밀번호를 다시 입력하세요"
          :required="true"
          :readonly="isLoading"
          :class="formErrors.confirmPassword ? 'border-red-500' : ''"
        />
        <p v-if="formErrors.confirmPassword" class="text-red-500 text-sm mt-1">
          {{ formErrors.confirmPassword }}
        </p>
      </div>

      <!-- 메시지 표시 -->
      <div
        v-if="message"
        :class="[
          'text-center text-sm p-3 rounded-lg whitespace-pre-line',
          message.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700',
        ]"
      >
        {{ message.text }}
      </div>
    </div>

    <!-- 버튼들 - 화면 하단 고정 -->
    <div class="flex gap-3 pt-4 pb-10">
      <PrimaryBtn
        text="회원가입"
        :disabled="isLoading"
        :isLoading="isLoading"
        @click="handleStep2"
        class="flex-1 py-3"
      />
    </div>
  </div>
</template>
