package com.example.loginservice.config.security;

import com.example.loginservice.config.jwt.TokenProviderService;
import com.example.loginservice.enums.Role;
import com.example.loginservice.model.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.example.loginservice.service.EmailService;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.example.loginservice", "com.example.service"})
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final EmailService emailService;


    private final TokenProviderService tokenProviderService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    @Value("${return.redirect-url}")
    String redirectURL;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // OAuth2User 정보를 가져오기
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId(); // Google, Kakao, Naver 등
        System.out.println("Registration Id: " + registrationId);
        System.out.println("Oauth2User: " + oAuth2User);

        // OAuth2AuthorizedClient를 통해 Access Token 가져오기
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                registrationId, token.getName()
        );
        if (authorizedClient == null) {
            throw new IllegalStateException("OAuth2AuthorizedClient not found for: " + registrationId);
        }

        OAuth2AccessToken oAuth2AccessToken = authorizedClient.getAccessToken();
        String providerAccessToken = oAuth2AccessToken.getTokenValue(); // 제공자의 Access Token
        System.out.println("Provider Access Token: " + providerAccessToken);

        // 사용자 정보를 제공자별로 다르게 처리
        Member member = getUserDetails(oAuth2User, registrationId);
        System.out.println("getUserName: " + member.getUserName());
        //db에 저장
        emailService.insertMember(member);



        // JWT 토큰 생성
        String accessToken = tokenProviderService.generateToken(member, Duration.ofHours(2));
        String refreshToken = tokenProviderService.generateToken(member, Duration.ofDays(2));
        System.out.println("Refresh Token: " + refreshToken);
        System.out.println("accessToken: " + accessToken);

// JWT 토큰을 쿠키에 저장
        setCookie(response, "accessToken", accessToken);
        setCookie(response, "refreshToken", refreshToken);

// 프론트엔드 콜백 URL로 리디렉션
        String redirectUrl = redirectURL+"/login/callback"; // 프론트엔드 콜백 URL
        response.sendRedirect(redirectUrl);
    }
    private void setCookie(HttpServletResponse response, String cookieName, String token) {
        // 쿠키 생성
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가
        cookie.setSecure(true);    // HTTPS에서만 전송 (로컬 테스트 시 false로 변경 가능)
        cookie.setPath("/");       // 쿠키 유효 경로 설정
        cookie.setMaxAge(60 * 60 * 24 * 7); // 1주일 간 유지 (옵션)

        // SameSite 속성 설정
        String cookieValue = cookieName + "=" + token + "; Path=/; Max-Age=" + cookie.getMaxAge() + "; HttpOnly; Secure; SameSite=None";
        response.addHeader("Set-Cookie", cookieValue);
    }
    private Member getUserDetails(OAuth2User oAuth2User, String registrationId) {
        String userId = null; // OAuth2 제공자에서 가져온 고유 ID
        String email = null; // 이메일 주소
        String nickname = null; // 사용자 닉네임

        // OAuth2 제공자에 따른 사용자 정보 처리
        Map<String, Object> attributes = oAuth2User.getAttributes();

        switch (registrationId) {
            case "google":
                email = (String) attributes.get("email");
                userId = (String) attributes.get("id"); // Google의 고유 사용자 ID
                nickname = (String) attributes.get("name"); // Google 사용자의 이름
                break;
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                System.out.println("kakaoAccount : " + kakaoAccount);
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");
                    System.out.println("email : " + email);
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    if (profile != null) {
                        nickname = (String) profile.get("nickname");
                    }
                } else {
                    throw new IllegalStateException("Kakao account details are missing.");
                }
                userId = String.valueOf(attributes.get("id"));
                break;
            case "naver":
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                if (naverResponse != null) {
                    email = (String) naverResponse.get("email");
                    nickname = (String) naverResponse.get("name");
                    userId = String.valueOf(naverResponse.get("id"));
                } else {
                    throw new IllegalStateException("Naver account details are missing.");
                }
                break;
            default:
                throw new IllegalStateException("Unknown registrationId: " + registrationId);
        }

        if (userId == null || email == null || nickname == null) {
            throw new IllegalStateException("OAuth2 user information is missing required attributes.");
        }

        // 사용자 객체 생성
        return Member.builder()
                .userName(userId) // 사용자 닉네임
                .nickName(nickname) // 필요시 userName과 동일하게 설정
                .userId(email) // 이메일
                .emailProvider(registrationId) // OAuth2 제공자 ID
                .role(Role.ROLE_USER) // 기본 역할 설정
                .build();
    }

}
