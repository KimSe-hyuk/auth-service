package com.example.loginservice.dto.find;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindUserResponseDTO {
    private boolean success;
    private final String message;

}
