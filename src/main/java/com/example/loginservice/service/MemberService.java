package com.example.loginservice.service;


import com.example.loginservice.config.jwt.TokenProviderService;
import com.example.loginservice.config.security.CustomUserDetails;
import com.example.loginservice.mapper.MemberMapper;
import com.example.loginservice.model.Member;
import com.example.loginservice.dto.login.SignInResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManager authenticationManager;
    private final MemberMapper memberMapper;
    private final TokenProviderService tokenProviderService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // 회원가입
    public void signUp(Member member ) {
        memberMapper.saveUser(member);
    }


    // 기존 로그인 처리 메서드
    public SignInResponseDTO signIn(String username, String password) {
        System.out.println("username"+username);
        System.out.println("password"+password);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("인증");
        Member member = ((CustomUserDetails) authentication.getPrincipal()).getMember();
        System.out.println("member");
        // Access Token
        String accessToken = tokenProviderService.generateToken(member, Duration.ofHours(2));
        System.out.println("accessToken");
        // Refresh Token
        String refreshToken = tokenProviderService.generateToken(member, Duration.ofDays(2));

        return SignInResponseDTO.builder()
                .isLoggedIn(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean checkId(String userId) {
        return memberMapper.checkUserIdExist(userId) == 0;
    }
    public boolean checkNickName(String nickName) {
        return memberMapper.checkNickNameExist(nickName) == 0;
    }
}
