import apiClient from './client.js';

// 사용자 관련 API 함수들

/**
 * 아이디 중복 체크
 * @param {string} loginId - 확인할 아이디
 * @returns {Promise<boolean>} 중복 여부 (true: 중복, false: 사용 가능)
 */
export const checkDuplicateId = async loginId => {
  try {
    const response = await apiClient.get(`/user/duplicationcheck`, {
      params: { loginId },
    });
    return response.data;
  } catch (error) {
    console.error('아이디 중복 체크 실패:', error);
    throw error;
  }
};

/**
 * 회원가입
 * @param {Object} joinData - 회원가입 데이터
 * @param {string} joinData.loginId - 아이디
 * @param {string} joinData.password - 비밀번호
 * @param {string} joinData.name - 이름
 * @param {string} joinData.phone - 전화번호
 * @returns {Promise<Object>} 회원가입 결과
 */
export const createUser = async joinData => {
  try {
    const response = await apiClient.post('/user/create', joinData);
    return response.data;
  } catch (error) {
    console.error('회원가입 실패:', error);
    throw error;
  }
};

/**
 * 로그인
 * @param {Object} loginData - 로그인 데이터
 * @param {string} loginData.loginId - 아이디
 * @param {string} loginData.password - 비밀번호
 * @returns {Promise<Object>} 로그인 결과
 */
export const loginUser = async loginData => {
  try {
    const response = await apiClient.post('/user/login', loginData);
    return response.data;
  } catch (error) {
    console.error('로그인 실패:', error);
    throw error;
  }
};

/**
 * 계좌번호로 사용자 조회
 * @param {string} accountNo - 계좌번호
 * @param {string} bankCode - 은행 코드
 * @returns {Promise<Object>} 사용자 정보
 */
export const getUserByAccount = async (accountNo, bankCode) => {
  try {
    const response = await apiClient.get(`/user/${accountNo}/${bankCode}`);
    return response.data;
  } catch (error) {
    console.error('사용자 조회 실패:', error);
    throw error;
  }
};
