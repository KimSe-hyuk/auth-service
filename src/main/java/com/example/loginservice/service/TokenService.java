package com.example.loginservice.service;

import com.example.loginservice.config.jwt.TokenProviderService;
import com.example.loginservice.dto.token.ClaimsResponseDTO;
import com.example.loginservice.dto.token.MemberDetailTokenResponseDTO;
import com.example.loginservice.dto.token.RefreshTokenResponseDTO;
import com.example.loginservice.dto.token.ValidTokenResponseDTO;
import com.example.loginservice.mapper.MemberMapper;
import com.example.loginservice.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private  final TokenProviderService tokenProviderService;
    private final MemberMapper memberMapper;
    public RefreshTokenResponseDTO refreshToken(String refreshToken) {
        int result = tokenProviderService.validateToken(refreshToken);
        String newAssessToken = null;
        String newRefreshToken = null;

        if( result==1 ){
            Member user = tokenProviderService.getTokenDetails(refreshToken);

            newAssessToken = tokenProviderService.generateToken(user, Duration.ofHours(2));
            newRefreshToken = tokenProviderService.generateToken(user, Duration.ofDays(2));
        }
        return RefreshTokenResponseDTO.builder()
                .validated(result==1)
                .accessToken(newAssessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public ValidTokenResponseDTO validToken(String token) {
        int result = tokenProviderService.validateToken(token);
        return ValidTokenResponseDTO.builder()
                .statusNum(result)
                .build();
    }

    public ClaimsResponseDTO getAuthentication(String token) {
        return tokenProviderService.getAuthentication(token);
    }
    @Transactional(readOnly = true)
    public MemberDetailTokenResponseDTO loginDetail(String token) {
        int validateToken = tokenProviderService.validateToken(token);
        if(validateToken==1){
            ClaimsResponseDTO authentication = tokenProviderService.getAuthentication(token);
            return MemberDetailTokenResponseDTO.builder()
                    .member(memberMapper.memberDetail(authentication.getUserId()))
                    .success(true)
                    .build();
        }else{
            return MemberDetailTokenResponseDTO.builder()
                    .member(null)
                    .success(true)
                    .build();
        }
    }
}
