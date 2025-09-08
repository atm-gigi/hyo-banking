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
  async getUserInfo(accountNo, bankCode) {
    try {
      const response = await apiClient.get(`/user/${accountNo}/${bankCode}`);

      atmStore.setTargetUserName(response.data.name);
      atmStore.setTargetUserId(response.data.userId);

      return response;
    } catch (error) {
      console.error('Error fetching user info:', error);
      throw error;
    }
  },
};

export default UserAPI;
