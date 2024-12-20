package com.example.loginservice.service;


import com.example.loginservice.config.jwt.TokenProviderService;
import com.example.loginservice.config.security.CustomUserDetails;
import com.example.loginservice.mapper.MemberMapper;
import com.example.loginservice.model.Member;
import com.example.loginservice.dto.login.SignInResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    public SignInResponseDTO signIn(String userId, String password) {
        System.out.println("아이디: " + userId);
        System.out.println("비밀번호: " + password);

        try {
            // 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, password)
            );

            // 인증 후 SecurityContext에 인증 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("인증 성공");

            // 인증된 사용자 정보 가져오기
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = customUserDetails.getMember();

            // 인증된 사용자 정보 출력
            System.out.println("인증된 회원 정보: " + member.getUserId());

            // Access Token 생성
            String accessToken = tokenProviderService.generateToken(member, Duration.ofHours(2));
            System.out.println("Access Token 생성 완료");

            // Refresh Token 생성
            String refreshToken = tokenProviderService.generateToken(member, Duration.ofDays(2));
            System.out.println("Refresh Token 생성 완료");

            // 로그인 성공 응답 반환
            return SignInResponseDTO.builder()
                    .isLoggedIn(true)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            // 인증 실패 시 SignInResponseDTO 반환
            System.out.println("인증 실패: 잘못된 아이디나 비밀번호");

            return SignInResponseDTO.builder()
                    .isLoggedIn(false)
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
        } catch (Exception e) {
            // 기타 예외 처리 시 SignInResponseDTO 반환
            System.out.println("인증 오류 발생: " + e.getMessage());

            return SignInResponseDTO.builder()
                    .isLoggedIn(false)
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
        }
    }



    public boolean checkId(String userId) {
        return memberMapper.checkUserIdExist(userId) == 0;
    }
    public boolean checkNickName(String nickName) {
        return memberMapper.checkNickNameExist(nickName) == 0;
    }
}
