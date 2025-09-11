import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';
import { atmTransactionStore } from '@/stores/atmTransactionStore';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
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
    const {
      txnType,
      sourceBankCode,
      sourceAccountNo,
      targetBankCode,
      targetAccountNo,
      amount,
      currencyCode,
      description,
    } = transactionData;

    try {
      // 요청을 보내기 직전에 고유한 멱등성 키를 생성합니다.
      const idempotencyKey = uuidv4();
      console.log('Generated Idempotency-Key:', idempotencyKey); // 확인용 로그

      const response = await apiClient.post(
        '/transactions',
        {
          txnType: txnType.toUpperCase(),
          sourceBankCode,
          sourceAccountNo,
          targetBankCode,
          targetAccountNo,
          amount,
          currencyCode,
          description,
        },
        {
          headers: {
            'Idempotency-Key': idempotencyKey,
          },
        }
      );
      atmStore.setTxnId(response.data.txnId);

      return response;
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
