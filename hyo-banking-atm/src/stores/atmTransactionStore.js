import { ref, computed } from 'vue';
import { defineStore } from 'pinia';

export const atmTransactionStore = defineStore('atmTransactionInfo', () => {
  const userId = ref(1);
  const accountId = ref(1);
  const txnId = ref(null);
  const accountNo = ref('110-123-456789');
  const currencyCode = ref('KRW');
  const bankCode = ref('KB');
  const balance = ref(0);
  const amount = ref(0);
  const task = ref('');
  const targetBankCode = ref('');
  const targetAccountNo = ref('');
  const targetUserName = ref('');
  const targetUserId = ref(null);
  const description = ref('');

  // 2. getters -> computed()
  // getters는 computed()를 사용하여 계산된 값을 만듭니다.
  const formattedBalance = computed(() => {
    const currentBalance = balance.value ?? 0;
    return currentBalance.toLocaleString('ko-KR') + '원';
  });

  const formattedAmount = computed(() => {
    return amount.value.toLocaleString('ko-KR') + '원';
  });

  const isTargetAccountSet = computed(() => {
    return targetBankCode.value !== '' && targetAccountNo.value !== '';
  });

  // 3. actions -> function()
  // actions는 state를 변경하는 일반 함수로 선언합니다.
  function setTargetAccountNo(newAccountNo) {
    targetAccountNo.value = newAccountNo;
  }

  function setTargetBankCode(newBankCode) {
    targetBankCode.value = newBankCode;
  }

  function setTxnId(newTxnId) {
    txnId.value = newTxnId;
  }

  function setTaskType(newTask) {
    task.value = newTask;
  }

  function setAmount(newAmount) {
    amount.value = newAmount;
  }

  function setDescription(newDescription) {
    description.value = newDescription;
  }

  function setTargetUserId(newUserId) {
    targetUserId.value = newUserId;
  }

  function setTargetUserName(newUserName) {
    targetUserName.value = newUserName;
  }

  function setBalance(newBalance) {
    balance.value = newBalance;
  }

  function resetTransaction() {
    txnId.value = null;
    amount.value = 0;
    targetBankCode.value = '';
    targetAccountNo.value = '';
    description.value = '';
    task.value = '';
    balance.value = 0;
    targetUserName.value = null;
  }

  // 4. 외부에서 사용할 수 있도록 모두 return
  // 컴포넌트에서 사용하려는 모든 변수와 함수를 return 해주어야 합니다.
  return {
    userId,
    txnId,
    bankCode,
    balance,
    accountNo,
    currencyCode,
    amount,
    task,
    accountId,
    targetBankCode,
    targetAccountNo,
    targetUserName,
    formattedBalance,
    formattedAmount,
    isTargetAccountSet,
    setTargetAccountNo,
    setTargetUserName,
    setTxnId,
    setTaskType,
    setAmount,
    setDescription,
    resetTransaction,
    setTargetUserId,
    setTargetBankCode,
    setBalance,
  };
});
