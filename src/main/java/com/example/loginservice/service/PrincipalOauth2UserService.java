package com.example.loginservice.service;//package com.example.spring.apringbootsecuritykimseheak.config.filter;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final HttpSession session;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본적으로 OAuth2User를 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User 입니다 : " + oAuth2User.getAttributes());

        // 로그인 제공자 식별 (구글 또는 카카오)
        String provider = userRequest.getClientRegistration().getRegistrationId(); // 구글 또는 카카오
        System.out.println("로그인 제공자" + provider);

        // 고유 사용자 ID를 얻기 위한 필드 이름 (구글: "sub", 카카오: "id")
        String id = null;

        // OAuth2User의 기존 속성을 수정하려면 Map을 가져옴
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        if ("google".equals(provider)) {
            // Google 로그인 처리: "sub" 필드에서 사용자 ID 추출
            Object googleId = oAuth2User.getAttributes().get("sub"); // 구글에서 받은 "sub" 필드 (사용자 ID)

            if (googleId instanceof Long) {
                id = String.valueOf(googleId);  // Long을 String으로 변환
            } else if (googleId instanceof String) {
                id = (String) googleId;  // 이미 String이면 그대로 사용
            }

            attributes.put("id", id); // "id"로 변경하여 attributes에 추가
            attributes.remove("sub"); // 기존의 "sub" 필드를 삭제
        } else if ("kakao".equals(provider)) {
            // Kakao 로그인 처리: "id" 필드에서 사용자 ID 추출
            Object kakaoId = oAuth2User.getAttributes().get("id"); // 카카오에서 받은 "id" 필드 (사용자 ID)

            if (kakaoId instanceof Long) {
                id = String.valueOf(kakaoId);  // Long을 String으로 변환
            } else if (kakaoId instanceof String) {
                id = (String) kakaoId;  // 이미 String이면 그대로 사용
            }

            attributes.put("id", id); // "id"로 변경하여 attributes에 추가

            // 카카오 로그인 시 AccessToken을 세션에 저장
            String kakaoAccessToken = userRequest.getAccessToken().getTokenValue();
            session.setAttribute("kakaoAccessToken", kakaoAccessToken);
        }else if ("naver".equals(provider)) {
            // Kakao 로그인 처리: "id" 필드에서 사용자 ID 추출
            Object naverId = oAuth2User.getAttributes().get("resultcode"); // 카카오에서 받은 "id" 필드 (사용자 ID)

            if (naverId instanceof Long) {
                id = String.valueOf(naverId);  // Long을 String으로 변환
            } else if (naverId instanceof String) {
                id = (String) naverId;  // 이미 String이면 그대로 사용
            }

            attributes.put("id", id); // "id"로 변경하여 attributes에 추가

            // 카카오 로그인 시 AccessToken을 세션에 저장
            String kakaoAccessToken = userRequest.getAccessToken().getTokenValue();
            session.setAttribute("kakaoAccessToken", kakaoAccessToken);
        }

        System.out.println("id입니다. "+ id);
        // 최종적으로 OAuth2User 객체를 반환 (수정된 attributes 사용)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), // 권한 설정
                attributes, // 수정된 사용자 정보
                "id" // 'id'를 사용자 정보에서 이름을 나타내는 속성으로 사용
        );
    }

}
