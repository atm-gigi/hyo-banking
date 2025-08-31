package user.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import user.domain.VerificationCode;
import user.repository.VerificationCodeRepository;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final VerificationCodeRepository codeRepository;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String fromPhoneNumber;

    public void sendSms(String toPhoneNumber) {
        DefaultMessageService messageService =  NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
        String randomNum = generateRandomNumber();
        // Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
        Message message = new Message();
        message.setFrom(fromPhoneNumber);
        message.setTo(toPhoneNumber);
        message.setText("인증번호는 [" + randomNum + "] 입니다.");

        try {
            // send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
            VerificationCode code = VerificationCode.builder()
                    .phone(toPhoneNumber)
                    .code(randomNum)
                    .build();
            codeRepository.save(code); // 인증 코드 저장
            messageService.send(message); // 인증코드 전송
        } catch (NurigoMessageNotReceivedException exception) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다!
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    // 랜덤한 6자리 숫자 생성
    private String generateRandomNumber() {
        Random rand = new Random();
        StringBuilder numStr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            numStr.append(rand.nextInt(10));
        }
        return numStr.toString();
    }
}
