// 인증 관련 유틸리티 함수들

import { AuthStatus } from '../types/auth.js';

// 로그인 상태 검증
export const validateLoginState = authStore => {
  if (!authStore.isAuthenticated) {
    return { isValid: false, status: AuthStatus.LOGGED_OUT };
  }

  if (!authStore.checkTokenExpiry()) {
    authStore.logout();
    return { isValid: false, status: AuthStatus.LOGGED_OUT };
  }

  return { isValid: true, status: AuthStatus.LOGGED_IN };
};

// 로그인 폼 유효성 검사
export const validateLoginForm = credentials => {
  const errors = {};

  if (!credentials.username || credentials.username.trim() === '') {
    errors.username = '아이디를 입력해주세요.';
  } else if (credentials.username.length < 3) {
    errors.username = '아이디는 3자 이상 입력해주세요.';
  }

  if (!credentials.password || credentials.password.trim() === '') {
    errors.password = '비밀번호를 입력해주세요.';
  } else if (credentials.password.length < 4) {
    errors.password = '비밀번호는 4자 이상 입력해주세요.';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
};

// 로그인 정보 암호화 (간단한 Base64 인코딩)
export const encodeCredentials = credentials => {
  const credentialsString = `${credentials.username}:${credentials.password}`;
  return btoa(credentialsString);
};

// 로그인 정보 복호화
export const decodeCredentials = encodedCredentials => {
  try {
    const decoded = atob(encodedCredentials);
    const [username, password] = decoded.split(':');
    return { username, password };
  } catch (error) {
    console.error('Failed to decode credentials:', error);
    return null;
  }
};

// 토큰 만료 시간 계산
export const calculateTokenExpiry = (loginTime, expiresIn = 3600) => {
  const loginDate = new Date(loginTime);
  const expiryDate = new Date(loginDate.getTime() + expiresIn * 1000);
  return expiryDate;
};

// 토큰 만료까지 남은 시간 (분)
export const getTokenTimeRemaining = (loginTime, expiresIn = 3600) => {
  const expiryDate = calculateTokenExpiry(loginTime, expiresIn);
  const now = new Date();
  const timeRemaining = expiryDate.getTime() - now.getTime();

  if (timeRemaining <= 0) {
    return 0;
  }

  return Math.floor(timeRemaining / (1000 * 60)); // 분 단위로 반환
};

// 로그인 세션 유지 시간 포맷팅
export const formatSessionTime = loginTime => {
  if (!loginTime) return '알 수 없음';

  const loginDate = new Date(loginTime);
  const now = new Date();
  const diffMs = now.getTime() - loginDate.getTime();
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

  if (diffHours > 0) {
    return `${diffHours}시간 ${diffMinutes}분`;
  } else {
    return `${diffMinutes}분`;
  }
};

// 보안을 위한 민감한 정보 마스킹
export const maskSensitiveInfo = (info, type = 'default') => {
  if (!info) return '';

  switch (type) {
    case 'phone':
      return info.replace(/(\d{3})\d{4}(\d{4})/, '$1-****-$2');
    case 'account':
      return info.replace(/(\d{3})\d{3}(\d{6})/, '$1-***-$2');
    case 'email':
      const [username, domain] = info.split('@');
      const maskedUsername =
        username.length > 2 ? username.substring(0, 2) + '*'.repeat(username.length - 2) : username;
      return `${maskedUsername}@${domain}`;
    case 'name':
      if (info.length <= 2) return info;
      return info.substring(0, 1) + '*'.repeat(info.length - 2) + info.substring(info.length - 1);
    default:
      return info.length > 4
        ? info.substring(0, 2) + '*'.repeat(info.length - 4) + info.substring(info.length - 2)
        : info;
  }
};

// 로그인 시도 횟수 관리
export const createLoginAttemptManager = () => {
  const maxAttempts = 5;
  const lockoutDuration = 15 * 60 * 1000; // 15분

  const getAttempts = () => {
    const attempts = localStorage.getItem('login_attempts');
    return attempts ? JSON.parse(attempts) : { count: 0, lastAttempt: null };
  };

  const recordAttempt = (success = false) => {
    const attempts = getAttempts();

    if (success) {
      localStorage.removeItem('login_attempts');
      return { canAttempt: true, remainingAttempts: maxAttempts };
    }

    attempts.count += 1;
    attempts.lastAttempt = new Date().toISOString();
    localStorage.setItem('login_attempts', JSON.stringify(attempts));

    const remainingAttempts = maxAttempts - attempts.count;
    const canAttempt = remainingAttempts > 0;

    return { canAttempt, remainingAttempts };
  };

  const isLockedOut = () => {
    const attempts = getAttempts();
    if (attempts.count < maxAttempts) return false;

    const lastAttempt = new Date(attempts.lastAttempt);
    const now = new Date();
    const timeSinceLastAttempt = now.getTime() - lastAttempt.getTime();

    return timeSinceLastAttempt < lockoutDuration;
  };

  const getLockoutTimeRemaining = () => {
    if (!isLockedOut()) return 0;

    const attempts = getAttempts();
    const lastAttempt = new Date(attempts.lastAttempt);
    const lockoutEnd = new Date(lastAttempt.getTime() + lockoutDuration);
    const now = new Date();

    return Math.max(0, Math.floor((lockoutEnd.getTime() - now.getTime()) / 1000));
  };

  return {
    recordAttempt,
    isLockedOut,
    getLockoutTimeRemaining,
    getAttempts,
  };
};
