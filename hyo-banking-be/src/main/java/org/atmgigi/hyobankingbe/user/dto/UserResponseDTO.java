package org.atmgigi.hyobankingbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record UserResponseDTO (

    long userId,
    String name,
    String phone
) {}
