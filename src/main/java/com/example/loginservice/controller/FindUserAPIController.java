package com.example.loginservice.controller;

import com.example.loginservice.config.jwt.TokenProviderService;
import com.example.loginservice.dto.find.*;
import com.example.loginservice.enums.Role;
import com.example.loginservice.model.Member;
import com.example.loginservice.service.FindMemberService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/find/user")
public class FindUserAPIController {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url}")
    private String appUrl;
    private final FindMemberService findMemberService;

    private final JavaMailSender mailSender;
    private final TokenProviderService tokenProviderService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/find-id")
    public FindUserResponseDTO findId(@RequestBody FindEmailRequestDTO email){
        boolean checkEmail = findMemberService.checkEmail(email.getEmail());
        if(checkEmail){
            String userId = findMemberService.getUserId(email);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email.getEmail());
            message.setSubject("회원님에 id 입니다!");
            message.setText("회원 ID : "+userId);
            // 이메일 전송
            mailSender.send(message);
            return FindUserResponseDTO.builder().success(true).message("이메일 발송 성공!").build();
        }else{
            return FindUserResponseDTO.builder().success(false).message("이메일 발송 실패!").build();
        }
    }
    @PostMapping("/send-pw")
    public FindUserResponseDTO sendPasswordResetLink(@RequestBody FindUserIdEmailRequestDTO userIdEmailRequestDto) throws MessagingException {
        boolean checkEmail = findMemberService.checkEmailUserId(userIdEmailRequestDto);
        if(checkEmail){
            String token = tokenProviderService.generateToken(Member.builder().userId(userIdEmailRequestDto.getUserId()).role(Role.ROLE_USER).build(), Duration.ofMinutes(30));
            String resetLink =appUrl + "/reset-password?token=" + token;

            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // MimeMessageHelper를 사용하여 HTML 설정
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(userIdEmailRequestDto.getUserId());
            helper.setSubject("비밀번호 재설정 요청!");

            // HTML 내용 설정
            String htmlContent = "<p>회원님이 요청하신 pw 찾기 링크입니다:</p>" +
                    "<a href=\"" + resetLink + "\">다음 링크를 클릭하여 비밀번호를 재설정하세요:</a>";
            helper.setText(htmlContent, true); // true로 설정해야 HTML 형식으로 전송됨

            // 이메일 전송
            mailSender.send(mimeMessage);
            return FindUserResponseDTO.builder().success(true).message("이메일 발송 성공!").build();
        }else{
            return FindUserResponseDTO.builder().success(false).message("이메일 발송 실패!").build();
        }
    }
    @PostMapping("/update-pw")
    public FindUserResponseDTO resetPw(@RequestBody ResetPwTokenRequestDTO resetPwTokenRequestDTO){
        System.out.println("token"+ resetPwTokenRequestDTO.getResetToken());
        int validateToken = tokenProviderService.validateToken(resetPwTokenRequestDTO.getResetToken());
        System.out.println("validTOken : " + validateToken);
        if(validateToken==1){
            Member tokenDetails = tokenProviderService.getTokenDetails(resetPwTokenRequestDTO.getResetToken());
            System.out.println("userId : " + tokenDetails.getUserId());
            UpdatePwRequestDTO build = UpdatePwRequestDTO.builder().password(resetPwTokenRequestDTO.getPassword()).userId(tokenDetails.getUserId()).build();
            Member member = build.toMember(bCryptPasswordEncoder);
            if(findMemberService.resetPw(member)){
                System.out.println("비밀번호 변경 성공!");
                return FindUserResponseDTO.builder().success(true).build();
            }else{
                return FindUserResponseDTO.builder().success(false).message("비밀번호 업데이트 실패!").build();
            }
        }else{
            System.out.println("비밀번호 변경 실패!");
            return FindUserResponseDTO.builder().success(false).message("토큰 만료!").build();
        }
    }

}
