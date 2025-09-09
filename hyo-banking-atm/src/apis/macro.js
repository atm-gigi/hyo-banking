import apiClient from './client.js';

// 매크로 관련 API 함수들

// 매크로 생성
export const createMacro = async (userId, name) => {
  const response = await apiClient.post('/macros', {
    userId,
    name,
  });
  return response.data;
};

// 매크로 조회
export const getMacro = async macroId => {
  const response = await apiClient.get(`/macros/${macroId}`);
  return response.data;
};
// 매크로 단계 목록 조회
export const getMacroSteps = async macroId => {
  const response = await apiClient.get(`/macros/${macroId}/steps`);
  return response.data;
};

// QR 토큰 조회
export const getQrToken = async token => {
  const response = await apiClient.get(`/qr-tokens/${token}`);
  return response.data;
};

// QR 토큰 무효화
export const invalidateQrToken = async token => {
  await apiClient.post(`/qr-tokens/${token}/invalidate`);
};

// 매크로 실행 시작
export const startMacroExecution = async (macroId, qrToken) => {
  const response = await apiClient.post('/executions', {
    macroId,
    qrToken,
  });
  return response.data;
};

// 매크로 실행 조회
export const getMacroExecution = async executionId => {
  const response = await apiClient.get(`/executions/${executionId}`);
  return response.data;
};

// 매크로 실행 목록 조회
export const getMacroExecutions = async (
  macroId,
  status = null,
  from = null,
  to = null,
  page = 0,
  size = 20
) => {
  const params = { macroId, page, size };
  if (status) params.status = status;
  if (from) params.from = from;
  if (to) params.to = to;

  const response = await apiClient.get('/executions', { params });
  return response.data;
};
