import apiClient from './client.js';

// 계좌 관련 API 함수들

// 계좌 생성
export const createAccount = async (
  userId,
  bankCode,
  accountNo,
  accountType = 'USER',
  currencyCode = 'KRW'
) => {
  const response = await apiClient.post('/account/create', {
    userId,
    bankCode,
    accountNo,
    accountType,
    currencyCode,
  });
  return response.data;
};

// 사용자 계좌 목록 조회
export const getAccounts = async userId => {
  const response = await apiClient.get('/account', {
    params: { userId },
  });
  return response.data;
};

// 특정 계좌 조회
export const getAccount = async (accountId, userId) => {
  const response = await apiClient.get(`/account/${accountId}`, {
    params: { userId },
  });
  return response.data;
};

// 계좌 잔액 변경
export const updateBalance = async (accountId, newBalance) => {
  const response = await apiClient.put('/account/balance', {
    accountId,
    newBalance,
  });
  return response.data;
};
