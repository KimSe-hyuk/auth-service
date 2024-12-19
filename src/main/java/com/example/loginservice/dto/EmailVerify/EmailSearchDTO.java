package com.example.loginservice.dto.EmailVerify;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailSearchDTO {
    private String userName;
    private String emailProvider;

}
