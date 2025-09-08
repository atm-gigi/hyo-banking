import axios from 'axios';
import { atmTransactionStore } from '@/stores/atmTransactionStore';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

const atmStore = atmTransactionStore();

const TransactionAPI = {
  /**
   * Creates a new transaction.
   * @param {object} transactionData - The transaction details.
   * @returns {Promise<any>}
   */
  async createTransaction(transactionData) {
    const { txnType, targetBankCode, targetAccountNo, amount, currencyCode, description } =
      transactionData;

    atmStore.setAmount(amount);

    try {
      const response = await apiClient.post('/transactions', {
        txnType,
        targetBankCode,
        targetAccountNo,
        amount,
        currencyCode,
        description,
      });

      if (response.data.status !== 'SUCCESS') {
        return { error: 'Transaction failed', details: response.data };
      }

      atmStore.setTxnId(response.data.txnId);

      return response.data;
    } catch (error) {
      console.error('Error creating transaction:', error);
      throw error;
    }
  },

  /**
   * Fetches a single transaction and merges it with its latest entry.
   * @param {string} txnId - The ID of the transaction.
   * @returns {Promise<object>} The merged transaction data.
   */
  async getTransactionByTxnId(txnId) {
    try {
      const response = await apiClient.get(`/transactions/${txnId}`);

      // Axios automatically throws an error for non-2xx status codes,
      // so a manual status check is not necessary.

      const { entries, ...txnInfo } = response.data;

      if (!entries || entries.length === 0) {
        return txnInfo; // Return base info if no entries exist
      }

      const latestEntry = entries[0];

      // Merge the base transaction info with the latest entry
      const mergedData = { ...txnInfo, ...latestEntry };

      return mergedData;
    } catch (error) {
      console.error(`Error fetching transaction ${txnId}:`, error);
      throw error;
    }
  },

  /**
   * Fetche transactions and merges it with its latest entry.
   * @param {object} params - { from, to, accountNo, bankCode }
   * @returns {Promise<object>} The merged transaction data.
   */

  async getTransactionByAccountNo(params) {
    const { from, to, accountNo, bankCode } = params;
    try {
      const response = await apiClient.get('/transactions', {
        params: { from, to, accountNo, bankCode },
      });

      const { entries, ...txnInfo } = response.data;

      if (!entries || entries.length === 0) {
        return txnInfo; // Return base info if no entries exist
      }

      const latestEntry = entries[0];

      // Merge the base transaction info with the latest entry
      const mergedData = { ...txnInfo, ...latestEntry };

      return mergedData;
    } catch (error) {
      console.error('Error fetching transactions by account:', error);
      throw error;
    }
  },
};

export default TransactionAPI;
