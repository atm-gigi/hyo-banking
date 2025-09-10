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

  if (!credentials.loginId || credentials.loginId.trim() === '') {
    errors.loginId = '아이디를 입력해주세요.';
  } else if (credentials.loginId.length < 3) {
    errors.loginId = '아이디는 3자 이상 입력해주세요.';
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
  const credentialsString = `${credentials.loginId}:${credentials.password}`;
  return btoa(credentialsString);
};

// 로그인 정보 복호화
export const decodeCredentials = encodedCredentials => {
  try {
    const decoded = atob(encodedCredentials);
    const [loginId, password] = decoded.split(':');
    return { loginId, password };
  } catch (error) {
    console.error('Failed to decode credentials:', error);
    return null;
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
    case 'email': {
      const [username, domain] = info.split('@');
      const maskedUsername =
        username.length > 2 ? username.substring(0, 2) + '*'.repeat(username.length - 2) : username;
      return `${maskedUsername}@${domain}`;
    }
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
