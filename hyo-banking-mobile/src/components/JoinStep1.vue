<script setup lang="ts">
  import { ref, reactive, computed, onUnmounted } from 'vue';
  import TextInput from '@/components/TextInput.vue';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import { sendVerificationCode, verifyCode } from '@/apis';

  const emit = defineEmits(['next', 'update:formData']);

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
    name: '',
    phone: '',
  });
  const message = ref(null);

  // 휴대폰 인증 관련 상태
  const phoneVerification = reactive({
    isVerified: false,
    isSending: false,
    isVerifying: false,
    verificationCode: '',
    timeRemaining: 0,
    canResend: false,
    timer: null,
  });

  // 인증번호 재전송 가능 여부
  const canResendCode = computed(() => {
    return phoneVerification.canResend && !phoneVerification.isSending;
  });

  // 1단계 폼 유효성 검사 (이름, 전화번호)
  const validateStep1Form = data => {
    const errors = {};

    // 이름 검증
    if (!data.name || data.name.trim() === '') {
      errors.name = '이름을 입력해주세요.';
    } else if (data.name.length < 2) {
      errors.name = '이름은 2자 이상 입력해주세요.';
    } else if (!/^[가-힣a-zA-Z\s]+$/.test(data.name)) {
      errors.name = '이름은 한글, 영문만 사용 가능합니다.';
    }

    // 전화번호 검증
    if (!data.phone || data.phone.trim() === '') {
      errors.phone = '전화번호를 입력해주세요.';
    } else if (!/^010-\d{4}-\d{4}$/.test(data.phone)) {
      errors.phone = '전화번호는 010-0000-0000 형식으로 입력해주세요.';
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors,
    };
  };

  // 전화번호 포맷팅
  const formatPhoneNumber = value => {
    // 숫자만 추출
    const numbers = value.replace(/\D/g, '');

    // 010으로 시작하는 11자리 숫자인지 확인
    if (numbers.length === 11 && numbers.startsWith('010')) {
      return numbers.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    }

    // 10자리인 경우 010 추가
    if (numbers.length === 10 && numbers.startsWith('010')) {
      return numbers.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
    }

    return value;
  };

  // 전화번호 입력 처리
  const handlePhoneInput = event => {
    const formatted = formatPhoneNumber(event.target.value);
    emit('update:formData', { ...props.formData, phone: formatted });
  };

  // 1단계 처리 (이름, 전화번호)
  const handleStep1 = () => {
    // 폼 에러 초기화
    Object.keys(formErrors).forEach(key => delete formErrors[key]);
    message.value = null;

    // 1단계 폼 유효성 검사
    const validation = validateStep1Form(props.formData);
    if (!validation.isValid) {
      Object.assign(formErrors, validation.errors);

      const validationErrors = [];
      if (validation.errors.name) validationErrors.push('이름: ' + validation.errors.name);
      if (validation.errors.phone) validationErrors.push('전화번호: ' + validation.errors.phone);

      message.value = {
        type: 'error',
        text: validationErrors.join('\n'),
      };
      return;
    }

    // 휴대폰 인증 검증
    if (!phoneVerification.isVerified) {
      if (phoneVerification.timeRemaining > 0) {
        message.value = {
          type: 'error',
          text: '휴대폰 인증번호를 입력하고 인증을 완료해주세요.',
        };
      } else {
        message.value = {
          type: 'error',
          text: '휴대폰 인증번호를 전송하고 인증을 완료해주세요.',
        };
      }
      return;
    }

    // 2단계로 이동
    emit('next');
  };

  // 휴대폰 인증번호 전송
  const handleSendVerificationCode = async () => {
    if (!props.formData.phone || !/^010-\d{4}-\d{4}$/.test(props.formData.phone)) {
      message.value = {
        type: 'error',
        text: '올바른 전화번호를 입력해주세요.',
      };
      return;
    }

    phoneVerification.isSending = true;
    message.value = null;

    try {
      await sendVerificationCode(props.formData.phone);

      phoneVerification.timeRemaining = 300; // 5분
      phoneVerification.canResend = false;

      // 타이머 시작
      phoneVerification.timer = setInterval(() => {
        phoneVerification.timeRemaining--;
        if (phoneVerification.timeRemaining <= 0) {
          clearInterval(phoneVerification.timer);
          phoneVerification.canResend = true;
        }
      }, 1000);

      message.value = {
        type: 'success',
        text: '인증번호가 전송되었습니다.',
      };
    } catch (error) {
      message.value = {
        type: 'error',
        text: error.message || '인증번호 전송에 실패했습니다.',
      };
    } finally {
      phoneVerification.isSending = false;
    }
  };

  // 인증번호 확인
  const handleVerifyCode = async () => {
    if (!phoneVerification.verificationCode || phoneVerification.verificationCode.length !== 6) {
      message.value = {
        type: 'error',
        text: '6자리 인증번호를 입력해주세요.',
      };
      return;
    }

    phoneVerification.isVerifying = true;
    message.value = null;

    try {
      await verifyCode({
        phone: props.formData.phone,
        code: phoneVerification.verificationCode,
      });

      phoneVerification.isVerified = true;
      clearInterval(phoneVerification.timer);

      message.value = {
        type: 'success',
        text: '휴대폰 인증이 완료되었습니다.',
      };
    } catch (error) {
      phoneVerification.isVerified = false;
      message.value = {
        type: 'error',
        text: error.message || '인증번호가 올바르지 않습니다.',
      };
    } finally {
      phoneVerification.isVerifying = false;
    }
  };

  // 시간 포맷팅 (MM:SS)
  const formatTime = seconds => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  // 컴포넌트 언마운트 시 타이머 정리
  onUnmounted(() => {
    if (phoneVerification.timer) {
      clearInterval(phoneVerification.timer);
    }
  });
</script>

<template>
  <div class="h-full flex flex-col">
    <div class="flex-1 space-y-6">
      <!-- 이름 -->
      <div class="mb-14">
        <TextInput
          id="name"
          :model-value="formData.name"
          @update:model-value="value => emit('update:formData', { ...formData, name: value })"
          type="text"
          name="name"
          text="이름"
          placeholder="이름을 입력하세요"
          :required="true"
          :readonly="isLoading"
          :class="formErrors.name ? 'border-red-500' : ''"
        />
        <p v-if="formErrors.name" class="text-red-500 text-sm mt-1">
          {{ formErrors.name }}
        </p>
      </div>

      <!-- 전화번호 -->
      <div>
        <div class="flex gap-2">
          <div class="flex-1">
            <TextInput
              id="phone"
              :model-value="formData.phone"
              type="tel"
              name="phone"
              text="전화번호"
              placeholder="010-0000-0000"
              :required="true"
              :readonly="isLoading || phoneVerification.isVerified"
              :class="[
                formErrors.phone ? 'border-red-500' : '',
                phoneVerification.isVerified ? 'border-green-500' : '',
              ]"
              @input="handlePhoneInput"
            />
          </div>
          <button
            v-if="!phoneVerification.isVerified"
            type="button"
            @click="handleSendVerificationCode"
            :disabled="
              phoneVerification.isSending ||
              !formData.phone ||
              !/^010-\d{4}-\d{4}$/.test(formData.phone)
            "
            :class="[
              'px-4 py-2 text-sm font-medium rounded-lg border transition-colors',
              phoneVerification.isSending ||
              !formData.phone ||
              !/^010-\d{4}-\d{4}$/.test(formData.phone)
                ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                : 'bg-kb-yellow-200 text-kb-brown-200 border-kb-yellow-200 hover:bg-kb-yellow-300',
            ]"
          >
            <span v-if="phoneVerification.isSending">전송중...</span>
            <span v-else>인증번호 전송</span>
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
            인증완료
          </div>
        </div>
        <p v-if="formErrors.phone" class="text-red-500 text-sm mt-1">
          {{ formErrors.phone }}
        </p>
      </div>

      <!-- 인증번호 입력 -->
      <div v-if="phoneVerification.timeRemaining > 0 || phoneVerification.isVerified">
        <div class="flex gap-2">
          <div class="flex-1">
            <TextInput
              id="verificationCode"
              v-model="phoneVerification.verificationCode"
              type="text"
              name="verificationCode"
              text="인증번호"
              placeholder="6자리 인증번호"
              :required="true"
              :readonly="isLoading || phoneVerification.isVerified"
              :class="phoneVerification.isVerified ? 'border-green-500' : ''"
              maxlength="6"
            />
          </div>
          <button
            v-if="!phoneVerification.isVerified"
            type="button"
            @click="handleVerifyCode"
            :disabled="
              phoneVerification.isVerifying || phoneVerification.verificationCode.length !== 6
            "
            :class="[
              'px-4 py-2 text-sm font-medium rounded-lg border transition-colors',
              phoneVerification.isVerifying || phoneVerification.verificationCode.length !== 6
                ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                : 'bg-kb-yellow-200 text-kb-brown-200 border-kb-yellow-200 hover:bg-kb-yellow-300',
            ]"
          >
            <span v-if="phoneVerification.isVerifying">확인중...</span>
            <span v-else>인증확인</span>
          </button>
        </div>

        <!-- 타이머 및 재전송 -->
        <div class="flex justify-between items-center mt-2">
          <div v-if="phoneVerification.timeRemaining > 0" class="text-sm text-kb-gray-100">
            남은 시간: {{ formatTime(phoneVerification.timeRemaining) }}
          </div>
          <button
            v-if="canResendCode"
            type="button"
            @click="handleSendVerificationCode"
            class="text-sm text-kb-yellow-200 hover:text-kb-yellow-300 underline"
          >
            인증번호 재전송
          </button>
        </div>
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

    <!-- 다음 단계 버튼 - 화면 하단 고정 -->
    <div class="pt-4 pb-10">
      <PrimaryBtn
        text="다음 단계"
        :disabled="isLoading"
        :isLoading="isLoading"
        @click="handleStep1"
        class="w-full py-3"
      />
    </div>
  </div>
</template>
