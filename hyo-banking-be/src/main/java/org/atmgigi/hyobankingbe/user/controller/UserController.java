package org.atmgigi.hyobankingbe.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.atmgigi.hyobankingbe.user.dto.UserJoinRequestDTO;
import org.atmgigi.hyobankingbe.user.dto.UserLoginRequestDTO;
import org.atmgigi.hyobankingbe.user.service.MessageService;
import org.atmgigi.hyobankingbe.user.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final MessageService messageService;
    private final UserService userService;

    // 회원가입
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserJoinRequestDTO joinDTO) {
        userService.createUser(joinDTO);
        return ResponseEntity.ok(null);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Long> loginUser(@RequestBody UserLoginRequestDTO loginDTO) {
        return ResponseEntity.ok(userService.loginUser(loginDTO));
    }

    // 아이디 중복 체크
    @GetMapping("/duplicationcheck")
    public ResponseEntity<Boolean> duplicationCheckId(@RequestParam("loginId") String loginId) {
        return ResponseEntity.ok(userService.existUserId(loginId));
    }

    // 핸드폰 번호 인증메세지 전송
    @PostMapping("/message/send")
    public ResponseEntity<?> sendMessage() {
//        messageService.sendSms();
        return ResponseEntity.ok(null);
    }

    // 핸드폰 번호 인증
    @GetMapping("/message/verification")
    public ResponseEntity<?> verifyCode() {
        return ResponseEntity.ok(null);
    }
}
