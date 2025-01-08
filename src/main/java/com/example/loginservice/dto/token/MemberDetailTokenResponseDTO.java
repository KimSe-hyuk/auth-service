package com.example.loginservice.dto.token;

import com.example.loginservice.model.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDetailTokenResponseDTO {
    private Member member;
    private boolean success;
}
