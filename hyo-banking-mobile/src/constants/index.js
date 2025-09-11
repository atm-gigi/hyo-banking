export const TASK_TYPES = {
  DEPOSIT: 'deposit', // 입금
  WITHDRAW: 'withdraw', // 출금
  TRANSFER: 'transfer', // 송금,
};

// 매크로 단계 타입 (서버와 동일)
export const MACRO_STEP_TYPES = {
  BALANCE_CHECK: 'BALANCE_CHECK', // 잔액 조회
  WITHDRAW: 'WITHDRAW', // 출금
  DEPOSIT: 'DEPOSIT', // 입금
  TRANSFER: 'TRANSFER', // 이체
};

// 통화 코드
export const CURRENCY_CODES = {
  KRW: 'KRW', // 한국 원
  USD: 'USD', // 미국 달러
  EUR: 'EUR', // 유로
  JPY: 'JPY', // 일본 엔
};

export const PAYMENT_TYPES = {
  CARD: 'card',
  BANKBOOK: 'bankbook',
};

export const STORAGE_KEYS = {
  TRANSACTIONS: 'transactions',
};

export const ICON_URLS = {
  QR_CODE: 'https://img.icons8.com/color/48/qr-code--v1.png',
  MONEY: 'https://img.icons8.com/fluency/48/money.png',
  MONEY_BOX: 'https://img.icons8.com/fluency/48/money-box.png',
  MONEY_TRANSFER: 'https://img.icons8.com/fluency/48/initiate-money-transfer.png',
  FORWARD: 'https://img.icons8.com/ios-glyphs/30/forward.png',
};

// 은행 정보
export const BANKS = [
  { code: 'KB', name: '국민은행' },
  { code: '002', name: '신한은행' },
  { code: '003', name: '우리은행' },
  { code: '004', name: '하나은행' },
  { code: '005', name: '농협은행' },
  { code: '006', name: '기업은행' },
  { code: '007', name: '수협은행' },
  { code: '008', name: '새마을금고' },
  { code: '009', name: '신협' },
  { code: '010', name: '우체국' },
  { code: '011', name: '카카오뱅크' },
  { code: '012', name: '토스뱅크' },
  { code: '013', name: '케이뱅크' },
  { code: '014', name: '대구은행' },
  { code: '015', name: '부산은행' },
  { code: '016', name: '경남은행' },
  { code: '017', name: '광주은행' },
  { code: '018', name: '전북은행' },
  { code: '019', name: '제주은행' },
  { code: '020', name: 'SC제일은행' },
  { code: '021', name: '씨티은행' },
  { code: '022', name: 'HSBC은행' },
  { code: '023', name: '도이치은행' },
  { code: '024', name: 'JP모건체이스' },
  { code: '025', name: '미즈호은행' },
  { code: '026', name: 'BNP파리바은행' },
  { code: '027', name: '중국은행' },
  { code: '028', name: '중국공상은행' },
  { code: '029', name: '산업은행' },
  { code: '030', name: '수출입은행' },
];

// 은행 코드로 은행 이름을 가져오는 함수
export const getBankNameByCode = bankCode => {
  const bank = BANKS.find(b => b.code === bankCode);
  return bank ? bank.name : '알 수 없는 은행';
};

// 은행 이름으로 은행 코드를 가져오는 함수
export const getBankCodeByName = bankName => {
  const bank = BANKS.find(b => b.name === bankName);
  return bank ? bank.code : null;
};
