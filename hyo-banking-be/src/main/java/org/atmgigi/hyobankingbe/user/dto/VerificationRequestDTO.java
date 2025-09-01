package org.atmgigi.hyobankingbe.user.dto;

import lombok.Builder;

@Builder
public record VerificationRequestDTO (
        String phone,
        String code
) {}
