package org.atmgigi.hyobankingbe.macro.domain;

public enum StepType {
    BALANCE_CHECK, //잔액 확인 (금액 필요 X, 출금 계좌 필요)
    WITHDRAW, //출금 (from 계좌 필요, 금액 필요)
    DEPOSIT, //입금 (to 계좌 필요, 금액 필요)
    TRANSFER //이체 (from + to 계좌)
}
