package org.atmgigi.hyobankingbe.macro.domain;

public enum MacroStatus { //한 매크로의 현재 상태
    ACTIVE,//현재 사용 가능하고 실행 가능한 상태
    DRAFT, // 작성 중, 아직 확정되지 않은 상태
    INACTIVE, // 비활성화, 더 이상 실행되지 않음
}
