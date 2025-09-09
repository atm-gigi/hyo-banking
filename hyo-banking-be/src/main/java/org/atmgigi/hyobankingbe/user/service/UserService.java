package org.atmgigi.hyobankingbe.user.service;

import org.atmgigi.hyobankingbe.user.dto.UserJoinRequestDTO;
import org.atmgigi.hyobankingbe.user.dto.UserLoginRequestDTO;
import org.atmgigi.hyobankingbe.user.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO createUser(UserJoinRequestDTO dto);

    boolean existUserId(String loginId);

    UserResponseDTO loginUser(UserLoginRequestDTO dto);

    UserResponseDTO getUserByAccount(String accountNo, String bankCode);
}
