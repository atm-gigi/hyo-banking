import apiClient from './client.js';

// 메시지 관련 API 함수들

/**
 * 인증번호 전송
 * @param {string} phone - 전화번호
 * @returns {Promise<string>} 전송 결과 메시지
 */
export const sendVerificationCode = async phone => {
  try {
    const response = await apiClient.post('/user/message/send', null, {
      params: { phone },
    });
    return response.data;
  } catch (error) {
    console.error('인증번호 전송 실패:', error);
    throw error;
  }
};

/**
 * 인증번호 검증
 * @param {Object} verificationData - 인증 데이터
 * @param {string} verificationData.phone - 전화번호
 * @param {string} verificationData.code - 인증번호
 * @returns {Promise<string>} 검증 결과 메시지
 */
export const verifyCode = async verificationData => {
  try {
    const response = await apiClient.post('/user/message/verification', verificationData);
    return response.data;
  } catch (error) {
    console.error('인증번호 검증 실패:', error);
    throw error;
  }
};
