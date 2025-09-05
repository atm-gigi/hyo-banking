package org.atmgigi.hyobankingbe.macro.dto;

//실행 관련 요청 DTO
public class ExecutionDtos {

    //실행 시작 요청
    public record StartExecReq(Long macro_id, String qr_token) {}

}
