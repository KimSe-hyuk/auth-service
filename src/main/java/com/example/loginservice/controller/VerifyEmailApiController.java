package com.example.loginservice.controller;

import com.example.loginservice.dto.EmailVerify.EmailRequestDTO;
import com.example.loginservice.dto.EmailVerify.EmailVerityResponseDTO;
import com.example.loginservice.dto.EmailVerify.VerificationRequestDTO;
import com.example.loginservice.service.FindMemberService;
import com.example.loginservice.service.SessionService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth/verity")
@RequiredArgsConstructor
public class VerifyEmailApiController {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final SessionService sessionService;
    private final FindMemberService findMemberService;


    // 인증번호 생성 및 이메일 전송
    @PostMapping("/send-verification-email")
    public EmailVerityResponseDTO sendVerificationEmail(@RequestHeader("Session-Id") String sessionId,@RequestBody EmailRequestDTO emailRequest) {
        boolean checkEmail = findMemberService.checkEmail(emailRequest.getEmail());
        if(checkEmail){
            return EmailVerityResponseDTO.builder()
                    .success(false)
                    .message("이미있는 이메일입니다!")
                    .build();
        }
        System.out.println("이메일 발송");
        System.out.println("Session ID: " + sessionId);

        try {
            // 인증번호 생성
            String verificationCode = generateVerificationCode();
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("verificationCode", verificationCode);
            sessionService.saveSession(sessionId,sessionData);
            // 이메일 내용 설정
            // MimeMessage 객체 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 제목
            helper.setSubject("회원가입 이메일 인증");

            // 발신자 이메일
            helper.setFrom(fromEmail);

            // 수신자 이메일
            helper.setTo(emailRequest.getEmail());

            // HTML 내용 작성
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "    <style>" +
                    "        .email-container {" +
                    "            font-family: Arial, sans-serif;" +
                    "            background-color: #f9f9f9;" +
                    "            padding: 20px;" +
                    "            border: 1px solid #ddd;" +
                    "            border-radius: 8px;" +
                    "            text-align: center;" +
                    "        }" +
                    "        .email-header {" +
                    "            font-size: 24px;" +
                    "            font-weight: bold;" +
                    "            color: #333;" +
                    "        }" +
                    "        .verification-code {" +
                    "            font-size: 20px;" +
                    "            color: #007bff;" +
                    "            font-weight: bold;" +
                    "        }" +
                    "        .email-footer {" +
                    "            margin-top: 20px;" +
                    "            font-size: 14px;" +
                    "            color: #777;" +
                    "        }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <div class=\"email-container\">" +
                    "        <div class=\"email-header\">회원가입 이메일 인증</div>" +
                    "        <p>아래의 인증 코드를 입력하여 이메일 인증을 완료하세요.</p>" +
                    "        <div class=\"verification-code\">" + verificationCode + "</div>" +
                    "        <p>감사합니다.</p>" +
                    "        <div class=\"email-footer\">본 이메일은 자동으로 발송되었습니다. 문의사항은 고객센터로 연락해주세요.</div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";

            // 이메일 본문 설정 (HTML)
            helper.setText(htmlContent, true);

            // 이메일 전송
            mailSender.send(message);

            System.out.println("이메일 전송 성공!");

            return EmailVerityResponseDTO.builder()
                    .success(true)
                    .message("인증번호가 이메일로 전송되었습니다!")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return EmailVerityResponseDTO.builder()
                    .success(false)
                    .message("이메일 전송 실패!")
                    .build();
        }
    }

    // 인증번호 확인
    @PostMapping("/verify-email")
    public EmailVerityResponseDTO verifyEmail(@RequestHeader("Session-Id") String sessionId,@RequestBody VerificationRequestDTO request) {
        // 세션에서 인증번호 가져오기
        String storedVerificationCode = sessionService.getSessionAttribute(sessionId, "verificationCode").toString();


        if (storedVerificationCode == null) {
            return EmailVerityResponseDTO.builder()
                    .success(false)
                    .message("인증번호가 비었습니다!")
                    .build();
        }

        // 인증번호 비교
        if (storedVerificationCode.equals(request.getVerificationCode())) {
            return EmailVerityResponseDTO.builder()
                    .success(true)
                    .message("인증 성공!")
                    .build();
        }

        return EmailVerityResponseDTO.builder()
                .success(false)
                .message("인증번호가 유효하지 않습니다!")
                .build();
    }

    // 랜덤 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
