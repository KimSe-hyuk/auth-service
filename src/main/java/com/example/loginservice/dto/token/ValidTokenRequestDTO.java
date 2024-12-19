package com.example.loginservice.dto.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidTokenRequestDTO {
    private String token;
}
