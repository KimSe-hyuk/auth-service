package com.example.loginservice.config.jwt;

import com.example.loginservice.enums.Role;
import com.example.loginservice.model.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProviderService {
    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expire) {
        Date now = new Date();
        return makeToken(member, new Date(now.getTime() + expire.toMillis()));
    }

    private String makeToken(Member member, Date expire) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expire)
                .setSubject(member.getUserId())
                .claim("role", member.getRole())
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();

    }

    private SecretKey getSecretKey() {
        byte[] decode = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }

    public int validateToken(String token) {
        // 1

        // 2

        // 3
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Token validated");
            return 1;
        }catch (ExpiredJwtException e){
            // 토큰이 만료된 경우
            log.info("token is expired");
            return 2;
        }catch (Exception e){
            // 복호화 과정에서 에러 발생
            log.info("token is not valid");
            return 3;
        }
    }

    public Member getTokenDetails(String token) {
        Claims claims = getClaims(token);
        return Member.builder()
                .userId(claims.getSubject())
                .role(Role.valueOf(claims.get("role",String.class)))
                .build();
    }


    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {

        Claims claims = getClaims(token);

        // Clamis에서 역할을 추출하고 , GrantedAuthority로 변환
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(String.valueOf(claims.get("role")))
        );

        User user = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(user,token,authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey()) // Assuming getSecreteKey() provides the secret key
                .build()
                .parseClaimsJws(token) // Parse the JWT token
                .getBody();
    }


}

