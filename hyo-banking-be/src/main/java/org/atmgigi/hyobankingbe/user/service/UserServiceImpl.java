package org.atmgigi.hyobankingbe.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Override
    public void createUser(UserJoinRequestDTO dto) {
        User user = User.builder()
                .loginId(dto.getLoginId())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .build();

        userRepository.save(user);
    }

    @Override
    public boolean existUserId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public long loginUser(UserLoginRequestDTO dto) {
        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(dto.getPassword());

        if(!userRepository.existsByLoginId(dto.getLoginId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 잘못입력하였습니다.");
        }
        if(!userRepository.existsByPassword(encodePassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 잘못입력하였습니다.");
        }

        User user = userRepository.findByLoginIdAndPassword(dto.getLoginId(), encodePassword)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));

        return user.getId();
    }
}
