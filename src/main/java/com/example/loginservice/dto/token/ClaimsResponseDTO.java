package com.example.loginservice.dto.token;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClaimsResponseDTO {
    private String userId;
    private String roles;
}
