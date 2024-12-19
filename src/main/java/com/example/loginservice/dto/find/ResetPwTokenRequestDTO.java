package com.example.loginservice.dto.find;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPwTokenRequestDTO {
    private String resetToken;
    private String password;
}
