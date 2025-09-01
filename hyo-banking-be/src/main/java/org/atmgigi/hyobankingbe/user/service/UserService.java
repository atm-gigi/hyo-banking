package org.atmgigi.hyobankingbe.user.service;

import org.atmgigi.hyobankingbe.user.dto.UserJoinRequestDTO;
import org.atmgigi.hyobankingbe.user.dto.UserLoginRequestDTO;

public interface UserService {

    void createUser(UserJoinRequestDTO dto);

    boolean existUserId(String loginId);

    long loginUser(UserLoginRequestDTO dto);
}
