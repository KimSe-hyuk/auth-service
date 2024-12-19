package com.example.loginservice.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignInResponseDTO {
    private boolean isLoggedIn;
    private String accessToken;
    private String refreshToken;
}
