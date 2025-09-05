package org.atmgigi.hyobankingbe.macro.dto;

//실행 관련 요청 DTO
public class ExecutionDTOs {

    //실행 시작 요청
    public record StartExecutionRequestDTO(Long macroId, String qrToken) {}

}
