package com.example.loginservice.dto.EmailVerify;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailVerityResponseDTO {
    private boolean success;
    private String message;

}
