import { ref, computed } from 'vue';
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', () => {
  // 상태
  const isLoggedIn = ref(false);
  const user = ref(null);
  const accessToken = ref(null);
  const refreshToken = ref(null);
  const loginTime = ref(null);

  // 계산된 속성
  const isAuthenticated = computed(() => isLoggedIn.value && user.value !== null);
  const userInfo = computed(() => user.value);
  const token = computed(() => accessToken.value);

  // 액션들
  const login = async credentials => {
    try {
      // 실제 API 호출을 시뮬레이션
      const response = await mockLoginAPI(credentials);

      if (response.success) {
        isLoggedIn.value = true;
        user.value = response.user;
        accessToken.value = response.accessToken;
        refreshToken.value = response.refreshToken;
        loginTime.value = new Date().toISOString();

        // 로컬 스토리지에 저장
        localStorage.setItem('auth_user', JSON.stringify(user.value));
        localStorage.setItem('auth_token', accessToken.value);
        localStorage.setItem('auth_refresh_token', refreshToken.value);
        localStorage.setItem('auth_login_time', loginTime.value);

        return { success: true, message: '로그인 성공' };
      } else {
        return { success: false, message: response.message || '로그인 실패' };
      }
    } catch (error) {
      console.error('Login error:', error);
      return { success: false, message: '로그인 중 오류가 발생했습니다.' };
    }
  };

  const logout = () => {
    isLoggedIn.value = false;
    user.value = null;
    accessToken.value = null;
    refreshToken.value = null;
    loginTime.value = null;

    // 로컬 스토리지에서 제거
    localStorage.removeItem('auth_user');
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_refresh_token');
    localStorage.removeItem('auth_login_time');
  };

  const refreshAccessToken = async () => {
    try {
      if (!refreshToken.value) {
        throw new Error('No refresh token available');
      }

      const response = await mockRefreshTokenAPI(refreshToken.value);

      if (response.success) {
        accessToken.value = response.accessToken;
        localStorage.setItem('auth_token', accessToken.value);
        return true;
      } else {
        logout();
        return false;
      }
    } catch (error) {
      console.error('Token refresh error:', error);
      logout();
      return false;
    }
  };

  const initializeAuth = () => {
    // 페이지 새로고침 시 로컬 스토리지에서 인증 정보 복원
    const storedUser = localStorage.getItem('auth_user');
    const storedToken = localStorage.getItem('auth_token');
    const storedRefreshToken = localStorage.getItem('auth_refresh_token');
    const storedLoginTime = localStorage.getItem('auth_login_time');

    if (storedUser && storedToken) {
      try {
        user.value = JSON.parse(storedUser);
        accessToken.value = storedToken;
        refreshToken.value = storedRefreshToken;
        loginTime.value = storedLoginTime;
        isLoggedIn.value = true;
      } catch (error) {
        console.error('Failed to restore auth state:', error);
        logout();
      }
    }
  };

  const checkTokenExpiry = () => {
    if (!loginTime.value) return false;

    const loginDate = new Date(loginTime.value);
    const now = new Date();
    const hoursSinceLogin = (now - loginDate) / (1000 * 60 * 60);

    // 24시간 후 토큰 만료로 간주
    return hoursSinceLogin < 24;
  };

  // 모의 API 함수들 (실제 구현에서는 실제 API 호출로 대체)
  const mockLoginAPI = async credentials => {
    // 실제 API 호출 시뮬레이션
    await new Promise(resolve => setTimeout(resolve, 1000));

    // 테스트용 더미 데이터
    if (credentials.username === 'test' && credentials.password === '1234') {
      return {
        success: true,
        user: {
          id: '1',
          username: 'test',
          name: '테스트 사용자',
          email: 'test@example.com',
          phone: '010-1234-5678',
          accountNumber: '123-456-789012',
          bankCode: 'KB',
          bankName: '국민은행',
        },
        accessToken: 'mock_access_token_' + Date.now(),
        refreshToken: 'mock_refresh_token_' + Date.now(),
      };
    } else {
      return {
        success: false,
        message: '아이디 또는 비밀번호가 올바르지 않습니다.',
      };
    }
  };

  const mockRefreshTokenAPI = async refreshToken => {
    await new Promise(resolve => setTimeout(resolve, 500));

    return {
      success: true,
      accessToken: 'refreshed_access_token_' + Date.now(),
    };
  };

  return {
    // 상태
    isLoggedIn,
    user,
    accessToken,
    refreshToken,
    loginTime,

    // 계산된 속성
    isAuthenticated,
    userInfo,
    token,

    // 액션
    login,
    logout,
    refreshAccessToken,
    initializeAuth,
    checkTokenExpiry,
  };
});
