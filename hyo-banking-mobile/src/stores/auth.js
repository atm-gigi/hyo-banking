import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { createUser, loginUser } from '@/apis';

export const useAuthStore = defineStore('auth', () => {
  // 상태
  const isLoggedIn = ref(false);
  const user = ref(null);

  // 계산된 속성
  const isAuthenticated = computed(() => isLoggedIn.value && user.value !== null);
  const userInfo = computed(() => user.value);

  // 사용자 정보 접근을 위한 computed 속성들
  const currentUser = computed(() => user.value);
  const userId = computed(() => user.value?.userId);
  const userName = computed(() => user.value?.name);
  const userPhone = computed(() => user.value?.phone);
  const userLoginId = computed(() => user.value?.loginId);

  // 액션들
  const login = async credentials => {
    try {
      // 실제 API 호출
      const response = await loginUser(credentials);

      if (response) {
        isLoggedIn.value = true;

        // 백엔드 응답 구조에 맞게 사용자 정보 설정
        user.value = {
          userId: response.userId,
          name: response.name,
          phone: response.phone,
          loginId: credentials.loginId, // 로그인 시 사용한 아이디 추가
        };

        // 로컬 스토리지에 저장
        localStorage.setItem('auth_user', JSON.stringify(user.value));

        return { success: true, message: '로그인 성공' };
      } else {
        return { success: false, message: '로그인 실패' };
      }
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, message: error.message || '로그인 중 오류가 발생했습니다.' };
    }
  };

  const logout = () => {
    isLoggedIn.value = false;
    user.value = null;

    // 로컬 스토리지에서 제거
    localStorage.removeItem('auth_user');
  };

  const join = async joinData => {
    try {
      // 실제 API 호출
      const response = await createUser(joinData);

      // 회원가입 성공 시 사용자 정보를 로컬 스토리지에 저장
      if (response) {
        const newUser = {
          userId: response.userId,
          name: response.name,
          phone: response.phone,
          loginId: joinData.loginId,
        };

        // 로컬 스토리지에 저장
        localStorage.setItem('auth_user', JSON.stringify(newUser));
      }

      return { success: true, message: '회원가입이 완료되었습니다.', data: response };
    } catch (error) {
      console.error('Join error:', error);
      return { success: false, message: error.message || '회원가입 중 오류가 발생했습니다.' };
    }
  };

  const initializeAuth = () => {
    // 페이지 새로고침 시 로컬 스토리지에서 인증 정보 복원
    const storedUser = localStorage.getItem('auth_user');

    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);

        // 사용자 정보 구조 검증
        if (parsedUser && parsedUser.userId && parsedUser.name) {
          user.value = parsedUser;
          isLoggedIn.value = true;

          console.log('✅ 인증 정보 복원 완료:', {
            userId: parsedUser.userId,
            name: parsedUser.name,
            loginId: parsedUser.loginId,
          });
        } else {
          console.warn('⚠️ 저장된 사용자 정보가 올바르지 않습니다.');
          logout();
        }
      } catch (error) {
        console.error('❌ 인증 정보 복원 실패:', error);
        logout();
      }
    }
  };

  return {
    // 상태
    isLoggedIn,
    user,

    // 계산된 속성
    isAuthenticated,
    userInfo,
    currentUser,
    userId,
    userName,
    userPhone,
    userLoginId,

    // 액션
    login,
    join,
    logout,
    initializeAuth,
  };
});
