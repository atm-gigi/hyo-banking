import axios from 'axios';
import { atmTransactionStore } from '@/stores/atmTransactionStore';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
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
      atmStore.setBalance(response.data.balanceCache);
      return response.data.balanceCache;
    } catch (error) {
      console.error('Error fetching my account info:', error);
      throw error;
    }
  },
};

export default UserAPI;
