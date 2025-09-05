package org.atmgigi.hyobankingbe.macro.domain;

public enum ExecutionStatus {
    PENDING, // 실행 요청만 들어온 상태, 아직 시작 x
    RUNNING, // 실제로 단계들이 실행 중인 상태
    SUCCEEDED, // 모든 단계가 성공적으로 끝난 상태
    FAILED // 실행 중 오류가 발생한 상태
}

