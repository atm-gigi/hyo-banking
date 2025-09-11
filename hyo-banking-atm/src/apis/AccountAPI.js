import axios from 'axios';
import { atmTransactionStore } from '@/stores/atmTransactionStore';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const atmStore = atmTransactionStore();

const AccountAPI = {
  /**
   * 계좌 잔액을 업데이트합니다.
   * @param {object} accountData - { userId, account, balance }
   * @returns {Promise<any>}
   */
  async updateAccount(accountData) {
    const { userId, account, balance } = accountData;

    try {
      const response = await apiClient.put('/account/balance', {
        userId,
        account,
        balance,
      });
      return response.data;
    } catch (error) {
      console.error('Error updating account balance:', error);
      throw error;
    }
  },

  /**
   * 새로운 계좌를 생성합니다.
   * @param {object} accountData - { userId, bankCode, accountNo, accountType, currencyCode }
   * @returns {Promise<any>}
   */
  async createAccount(accountData) {
    const { userId, bankCode, accountNo, accountType, currencyCode } = accountData;

    try {
      const response = await apiClient.post('/account/create', {
        userId,
        bankCode,
        accountNo,
        accountType,
        currencyCode,
      });
      return response.data;
    } catch (error) {
      console.error('Error creating account:', error);
      throw error;
    }
  },

  /**
   * 특정 사용자의 모든 계좌 정보를 가져옵니다.
   * @param {string} userId
   * @returns {Promise<any>}
   */
  async getAccount(userId) {
    try {
      const response = await apiClient.get('/account', {
        params: { userId },
      });
      // Axios는 2xx 범위가 아닌 상태 코드에 대해 자동으로 에러를 throw하므로,
      // 별도의 상태 코드 체크는 필요하지 않습니다.
      return response.data;
    } catch (error) {
      console.error('Error fetching account:', error);
      throw error;
    }
  },

  /**
   * 특정 계좌 ID로 계좌 정보를 가져옵니다.
   * @param {string} accountId
   * @returns {Promise<any>}
   */
  async getAccountByAccountId(accountId, userId) {
    try {
      const response = await apiClient.get(`/account/${accountId}`, {
        params: { userId },
      });
      return response.data;
    } catch (error) {
      console.error(`Error fetching account by ID ${accountId}:`, error);
      throw error;
    }
  },
};

export default AccountAPI;
