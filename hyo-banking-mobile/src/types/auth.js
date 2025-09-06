// 인증 관련 타입 정의

export const AuthStatus = {
  LOGGED_IN: 'logged_in',
  LOGGED_OUT: 'logged_out',
  LOADING: 'loading',
  ERROR: 'error',
};

export const UserRole = {
  CUSTOMER: 'customer',
  ADMIN: 'admin',
  MANAGER: 'manager',
};

// 사용자 정보 타입
export const createUser = userData => ({
  id: userData.id || '',
  username: userData.username || '',
  name: userData.name || '',
  email: userData.email || '',
  phone: userData.phone || '',
  accountNumber: userData.accountNumber || '',
  bankCode: userData.bankCode || 'KB',
  bankName: userData.bankName || '국민은행',
  role: userData.role || UserRole.CUSTOMER,
  createdAt: userData.createdAt || new Date().toISOString(),
  lastLoginAt: userData.lastLoginAt || new Date().toISOString(),
});

// 로그인 자격 증명 타입
export const createLoginCredentials = credentials => ({
  username: credentials.username || '',
  password: credentials.password || '',
  rememberMe: credentials.rememberMe || false,
});

// API 응답 타입
export const createApiResponse = data => ({
  success: data.success || false,
  message: data.message || '',
  data: data.data || null,
  error: data.error || null,
});

// 토큰 정보 타입
export const createTokenInfo = tokenData => ({
  accessToken: tokenData.accessToken || '',
  refreshToken: tokenData.refreshToken || '',
  expiresIn: tokenData.expiresIn || 3600, // 1시간
  tokenType: tokenData.tokenType || 'Bearer',
});
