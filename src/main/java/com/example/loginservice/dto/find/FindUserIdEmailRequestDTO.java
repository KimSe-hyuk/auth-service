package com.example.loginservice.dto.find;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserIdEmailRequestDTO {
    private String userId;
    private String email;
}
