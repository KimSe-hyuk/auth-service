package com.example.loginservice.config.security;

import com.example.loginservice.config.NaverConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LogoutHandler implements LogoutHandler {
    private final NaverConfig naverConfig;
    private final OAuth2AuthorizedClientService authorizedClientService;
    @Value("${return.redirect-url}")
    private String redirectURL;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String redirectUrl =redirectURL+"/login"; // 기본 리디렉션 URL
        System.out.println("로그아웃 시작");

        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();
            String accessToken = getAccessToken(oAuth2Token);

            System.out.println("OAuth2 Provider: " + registrationId);
            try {
                switch (registrationId) {
                    case "google" -> redirectUrl = handleGoogleLogout();
                    case "kakao" -> {
                        if (accessToken != null) {
                            handleKakaoLogout(accessToken);
                        } else {
                            System.err.println("Kakao 로그아웃 실패: 액세스 토큰이 없습니다.");
                        }
                    }
                    case "naver" -> {
                        if (accessToken != null) {
                            handleNaverLogout(accessToken);
                        } else {
                            System.err.println("Naver 로그아웃 실패: 액세스 토큰이 없습니다.");
                        }
                    }
                    default -> System.err.println("지원되지 않는 OAuth2 공급자: " + registrationId);
                }
            } catch (Exception e) {
                System.err.println("로그아웃 과정에서 오류 발생: " + e.getMessage());
            }
        }

        // 보안 컨텍스트와 세션 초기화 후 리디렉션
        try {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            clearCookies(response);
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            System.err.println("리디렉션 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * OAuth2 Access Token 가져오기
     */
    private String getAccessToken(OAuth2AuthenticationToken token) {
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        token.getAuthorizedClientRegistrationId(),
                        token.getName()
                );
        return authorizedClient != null && authorizedClient.getAccessToken() != null
                ? authorizedClient.getAccessToken().getTokenValue()
                : null;
    }

    /**
     * 구글 로그아웃 처리
     */
    private String handleGoogleLogout() {
        System.out.println("Google 로그아웃은 사용자 브라우저에서 직접 처리됩니다.");
        return redirectURL+"/login";
    }

    /**
     * 카카오 로그아웃 API 호출
     */
    private void handleKakaoLogout(String accessToken) {
        String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, new HttpEntity<>(headers), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Kakao logout successful: " + response.getStatusCode());
            } else {
                System.err.println("Kakao logout failed: " + response.getStatusCode() + " " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Kakao 로그아웃 API 호출 실패: " + e.getMessage());
        }
    }

    /**
     * 네이버 연결 끊기 처리
     */
    private void handleNaverLogout(String accessToken) {
        String url = "https://nid.naver.com/oauth2.0/token?grant_type=delete"
                + "&client_id=" + naverConfig.getClientId()
                + "&client_secret=" + naverConfig.getClientSecret()
                + "&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Naver account disconnected successfully.");
            } else {
                System.err.println("Failed to disconnect Naver account: " + response.getStatusCode() + " " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error while disconnecting Naver account: " + e.getMessage());
        }
    }

    /**
     * 쿠키 초기화
     */
    private void clearCookies(HttpServletResponse response) {
        Cookie[] cookies = {
                new Cookie("JSESSIONID", null),
                new Cookie("access_token", null),
                new Cookie("refresh_token", null)
        };
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }
}
