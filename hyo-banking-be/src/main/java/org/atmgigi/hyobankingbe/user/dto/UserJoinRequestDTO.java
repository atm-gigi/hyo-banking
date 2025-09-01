package org.atmgigi.hyobankingbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record UserJoinRequestDTO (
    String loginId,
    String password,
    String name,
    String phone
) {}
