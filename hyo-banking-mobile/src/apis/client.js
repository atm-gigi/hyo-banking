import axios from 'axios';

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터
apiClient.interceptors.request.use(
  config => {
    // 요청 전 로깅 (개발 환경에서만)
    if (import.meta.env.DEV) {
      console.log('🚀 API Request:', config.method?.toUpperCase(), config.url, config.data);
    }
    return config;
  },
  error => {
    console.error('❌ Request Error:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터
apiClient.interceptors.response.use(
  response => {
    // 응답 성공 로깅 (개발 환경에서만)
    if (import.meta.env.DEV) {
      console.log('✅ API Response:', response.status, response.config.url, response.data);
    }
    return response;
  },
  error => {
    // 에러 로깅
    console.error('❌ API Error:', error.response?.status, error.response?.data || error.message);

    // 에러 응답 처리
    if (error.response) {
      // 서버에서 응답을 받았지만 에러 상태
      const { status, data } = error.response;

      switch (status) {
        case 400:
          throw new Error(data?.message || '잘못된 요청입니다.');
        case 401:
          throw new Error('인증이 필요합니다.');
        case 403:
          throw new Error('접근 권한이 없습니다.');
        case 404:
          throw new Error('요청한 리소스를 찾을 수 없습니다.');
        case 500:
          throw new Error('서버 오류가 발생했습니다.');
        default:
          throw new Error(data?.message || '알 수 없는 오류가 발생했습니다.');
      }
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못함
      throw new Error('네트워크 오류가 발생했습니다. 연결을 확인해주세요.');
    } else {
      // 요청 설정 중 오류
      throw new Error('요청 처리 중 오류가 발생했습니다.');
    }
  }
);

export default apiClient;
