use hyo_banking_db;
update account
set bank_code='KB'
where id='1';

INSERT INTO account (
    balance_cache,
    created_at,
    user_id,
    account_no,
    bank_code,
    currency_code,
    account_type,
    pin_hash
) VALUES (
             5000000.00,                                      -- balance_cache
             NOW(6),                                    -- created_at
             1,                                         -- user_id (예시)
             '444444-44-44444',                         -- account_no
             'NH',                                      -- bank_code
             'KRW',                                     -- currency_code
             'ATM_CASH',                                -- account_type
             SHA2('1234', 256)                          -- pin_hash (예시: 1234를 해시)
         );

