package org.atmgigi.hyobankingbe.user.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.account.domain.Account;
import org.atmgigi.hyobankingbe.account.repository.AccountRepository;
import org.atmgigi.hyobankingbe.common.exception.DomainException;
import org.atmgigi.hyobankingbe.common.exception.ErrorCode;
import org.atmgigi.hyobankingbe.user.dto.UserResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.atmgigi.hyobankingbe.user.dto.UserJoinRequestDTO;
import org.atmgigi.hyobankingbe.user.dto.UserLoginRequestDTO;
import org.atmgigi.hyobankingbe.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public UserResponseDTO createUser(UserJoinRequestDTO dto) {
        User user = User.builder()
                .loginId(dto.loginId())
                .name(dto.name())
                .password(passwordEncoder.encode(dto.password()))
                .phone(dto.phone())
                .build();

        userRepository.save(user);

        return UserResponseDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public boolean existUserId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public UserResponseDTO loginUser(UserLoginRequestDTO dto) {
        User user = userRepository.findByLoginId(dto.loginId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(dto.password(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public UserResponseDTO getUserByAccount(final String accountNo, final String bankCode) {
        Account account = accountRepository.findByAccountNoAndBankCode(accountNo, bankCode)
                .orElseThrow(() -> new DomainException(ErrorCode.ACCOUNT_NOT_FOUND, "계좌 정보가 없습니다"));

        User user = account.getUser();
        return UserResponseDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }
}
