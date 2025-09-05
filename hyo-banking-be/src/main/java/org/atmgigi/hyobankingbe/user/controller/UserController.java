package org.atmgigi.hyobankingbe.user.controller;

import lombok.RequiredArgsConstructor;
import org.atmgigi.hyobankingbe.user.dto.UserResponseDTO;
import org.atmgigi.hyobankingbe.user.dto.VerificationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.atmgigi.hyobankingbe.user.dto.UserJoinRequestDTO;
import org.atmgigi.hyobankingbe.user.dto.UserLoginRequestDTO;
import org.atmgigi.hyobankingbe.user.service.MessageService;
import org.atmgigi.hyobankingbe.user.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final MessageService messageService;
    private final UserService userService;

    // 회원가입
    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserJoinRequestDTO joinDTO) {
        return ResponseEntity.ok( userService.createUser(joinDTO));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> loginUser(@RequestBody UserLoginRequestDTO loginDTO) {
        return ResponseEntity.ok(userService.loginUser(loginDTO));
    }

    // 아이디 중복 체크
    @GetMapping("/duplicationcheck")
    public ResponseEntity<Boolean> duplicationCheckId(@RequestParam("loginId") String loginId) {
        return ResponseEntity.ok(userService.existUserId(loginId));
    }

    // 핸드폰 번호 인증메세지 전송
    @PostMapping("/message/send")
    public ResponseEntity<String> sendMessage(@RequestParam("phone") String phone) {
        messageService.sendSms(phone);
        return ResponseEntity.ok("메세지가 전송되었습니다.");
    }

    // 핸드폰 번호 인증
    @PostMapping("/message/verification")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationRequestDTO verificationDTO) {
        if(messageService.verificationMessageCode(verificationDTO))
            return ResponseEntity.ok("인증이 완료되었습니다.");
        return ResponseEntity.ok("인증에 실패하였습니다.");
    }

    //계좌번호를 통한 유저 조회
    @GetMapping("/{accountNo}/{bankCode}")
    public ResponseEntity<UserResponseDTO> getUserByAccount(
            @PathVariable("accountNo") String accountNo,
            @PathVariable("bankCode") String bankCode
    ) {
        return ResponseEntity.ok(userService.getUserByAccount(accountNo, bankCode));
    }
}
