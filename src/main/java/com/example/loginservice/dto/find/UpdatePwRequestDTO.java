package com.example.loginservice.dto.find;

import com.example.loginservice.model.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@Builder
public class UpdatePwRequestDTO {
    private String password;
    private String userId;
    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
    }
}
