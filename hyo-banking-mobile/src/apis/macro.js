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

// 매크로 목록 조회
export const getMacros = async (userId, status = null, page = 0, size = 20) => {
  const params = { userId, page, size };
  if (status) params.status = status;

  const response = await apiClient.get('/macros', { params });
  return response.data;
};

// 매크로 수정
export const updateMacro = async (macroId, name, status) => {
  const response = await apiClient.patch(`/macros/${macroId}`, {
    name,
    status,
  });
  return response.data;
};

// 매크로 삭제
export const deleteMacro = async macroId => {
  await apiClient.delete(`/macros/${macroId}`);
};

// 매크로 단계 추가
export const addMacroStep = async (macroId, stepData) => {
  const response = await apiClient.post(`/macros/${macroId}/steps`, stepData);
  return response.data;
};

// 매크로 단계 수정/생성 (upsert)
export const upsertMacroStep = async (macroId, order, stepData) => {
  const response = await apiClient.put(`/macros/${macroId}/steps/${order}`, stepData);
  return response.data;
};

// 매크로 단계 목록 조회
export const getMacroSteps = async macroId => {
  const response = await apiClient.get(`/macros/${macroId}/steps`);
  return response.data;
};

// 매크로 단계 삭제
export const deleteMacroStep = async (macroId, order) => {
  await apiClient.delete(`/macros/${macroId}/steps/${order}`);
};

// QR 토큰 생성
export const createQrToken = async macroId => {
  const response = await apiClient.post(`/macros/${macroId}/qr-tokens`);
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
