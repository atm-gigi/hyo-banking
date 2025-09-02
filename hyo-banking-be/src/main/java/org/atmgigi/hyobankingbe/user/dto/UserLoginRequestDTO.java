package org.atmgigi.hyobankingbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record UserLoginRequestDTO (
    String loginId,
    String password
) {}
