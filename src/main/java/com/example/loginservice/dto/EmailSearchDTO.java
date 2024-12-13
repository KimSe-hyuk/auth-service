package com.example.loginservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class EmailSearchDTO {
    private String userName;
    private String emailProvider;

}
