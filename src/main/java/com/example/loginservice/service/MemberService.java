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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void signUp(Member member ) {
        memberMapper.saveUser(member);
    }


    // 기존 로그인 처리 메서드
    public SignInResponseDTO signIn(String userId, String password) {
        System.out.println("아이디: " + userId);
        Member a = memberMapper.findUserByUserId(userId);
        try {
            Member member = memberMapper.findUserByUserId(userId);
            if (member == null) {
                System.out.println("❌ 사용자 없음: " + userId);
                return SignInResponseDTO.builder().isLoggedIn(false).build();
            }
            // 🚀 로그인 시 비밀번호 비교
            boolean passwordMatches = bCryptPasswordEncoder.matches(password, member.getPassword());


            if (!passwordMatches) {
                return SignInResponseDTO.builder().isLoggedIn(false).build();
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, password) // 🚨 여기서 문제가 발생할 가능성 있음
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("✅ 인증 성공");

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Member authenticatedMember = customUserDetails.getMember();

            String accessToken = tokenProviderService.generateToken(authenticatedMember, Duration.ofHours(2));
            String refreshToken = tokenProviderService.generateToken(authenticatedMember, Duration.ofDays(2));

            return SignInResponseDTO.builder()
                    .isLoggedIn(true)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            System.out.println("❌ 인증 실패: 잘못된 아이디나 비밀번호");
            return SignInResponseDTO.builder().isLoggedIn(false).build();
        } catch (Exception e) {
            System.out.println("❌ 인증 오류 발생: " + e.getMessage());
            return SignInResponseDTO.builder().isLoggedIn(false).build();
        }
    }


    @Transactional(readOnly = true)
    public boolean checkId(String userId) {
        return memberMapper.checkUserIdExist(userId) == 0;
    }
    public boolean checkNickName(String nickName) {
        return memberMapper.checkNickNameExist(nickName) == 0;
    }
}
