package com.example.loginservice.controller;

import com.example.loginservice.dto.EmailVerify.EmailRequestDTO;
import com.example.loginservice.dto.EmailVerify.EmailVerityResponseDTO;
import com.example.loginservice.dto.EmailVerify.VerificationRequestDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/verity")
@RequiredArgsConstructor
public class VerifyEmailApiController {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    // 인증번호 생성 및 이메일 전송
    @PostMapping("/send-verification-email")
    public EmailVerityResponseDTO sendVerificationEmail(@RequestBody EmailRequestDTO emailRequest, HttpSession session) {
        System.out.println("이메일 발송");
        System.out.println("Session ID: " + session.getId());

        try {
            // 인증번호 생성
            String verificationCode = generateVerificationCode();
            System.out.println("verificationCode : " + verificationCode);

            // 인증번호를 세션에 저장
            session.setAttribute("verificationCode", verificationCode);
            System.out.println("session id : " + session.getAttribute("verificationCode"));

            // 이메일 내용 설정
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(emailRequest.getEmail());
//            message.setSubject("회원가입 이메일 인증");
//            message.setText("인증번호: " + verificationCode);

            // 이메일 전송
           // mailSender.send(message);

            return EmailVerityResponseDTO.builder()
                    .success(true)
                    .message("인증번호가 이메일로 전송되었습니다.")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return EmailVerityResponseDTO.builder()
                    .success(false)
                    .message("이메일 전송 실패.")
                    .build();
        }
    }

    // 인증번호 확인
    @PostMapping("/verify-email")
    public EmailVerityResponseDTO verifyEmail(@RequestBody VerificationRequestDTO request, HttpSession session) {
        System.out.println("인증 시작");
        System.out.println("Session ID: " + session.getId());

        // 세션에서 인증번호 가져오기
        String storedVerificationCode = (String) session.getAttribute("verificationCode");
        System.out.println("storedVerificationCode : " + storedVerificationCode);
        System.out.println("verityCode : " + request.getVerificationCode());

        if (storedVerificationCode == null) {
            return EmailVerityResponseDTO.builder()
                    .success(false)
                    .message("인증번호가 만료되었습니다.")
                    .build();
        }

        // 인증번호 비교
        if (storedVerificationCode.equals(request.getVerificationCode())) {
            return EmailVerityResponseDTO.builder()
                    .success(true)
                    .message("인증 성공")
                    .build();
        }

        return EmailVerityResponseDTO.builder()
                .success(false)
                .message("인증번호가 유효하지 않습니다.")
                .build();
    }

    // 랜덤 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
