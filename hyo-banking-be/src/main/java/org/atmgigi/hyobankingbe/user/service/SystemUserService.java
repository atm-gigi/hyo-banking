package org.atmgigi.hyobankingbe.user.service;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.user.domain.User;
import org.atmgigi.hyobankingbe.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemUserService {

    public static final String SYSTEM_LOGIN_ID = "__system__";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User getOrCreateSystem() {
        return userRepository.findByLoginId(SYSTEM_LOGIN_ID)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .loginId(SYSTEM_LOGIN_ID)
                                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                .name("SYSTEM")
                                .build()
                ));
    }
}