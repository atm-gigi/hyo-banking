package org.atmgigi.hyobankingbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequestDTO {

    private String loginId;
    private String password;
    private String name;
    private String phone;
}
