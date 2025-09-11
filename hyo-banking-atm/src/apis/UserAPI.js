import axios from 'axios';
import { atmTransactionStore } from '@/stores/atmTransactionStore';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const atmStore = atmTransactionStore();

const UserAPI = {
  async getUserId(accountNo, bankCode) {
    try {
      const response = await apiClient.get(`/user/${accountNo}/${bankCode}`);

      atmStore.setTargetUserName(response.data.name);
      atmStore.setTargetUserId(response.data.userId);

      return response.data.userId;
    } catch (error) {
      console.error('Error fetching user info:', error);
      throw error;
    }
  },

  async getBalance(userId) {
    try {
      const response = await apiClient.get(`/account`, {
        params: { userId },
      });
      console.log('Account info response:', response.data);
      // 1. 서버 응답이 배열이고, 비어있지 않은지 확인합니다.
      if (Array.isArray(response.data) && response.data.length > 0) {
        // 2. 배열의 첫 번째 항목에서 잔액(balanceCache)을 가져옵니다.
        const balance = response.data[0].balanceCache;

        // 3. 스토어에 잔액을 저장하고 값을 반환합니다.
        atmStore.setBalance(balance);
        return balance;
      } else {
        // 4. 계좌 정보가 없거나 응답이 예상과 다른 경우
        console.error('계좌 정보를 찾을 수 없거나 응답 형식이 올바르지 않습니다.');
        atmStore.setBalance(0); // 잔액을 0으로 초기화
        return 0; // 또는 null, undefined를 반환하여 에러 처리
      }
    } catch (error) {
      console.error('Error fetching my account info:', error);
      throw error;
    }
  },
};

export default UserAPI;
