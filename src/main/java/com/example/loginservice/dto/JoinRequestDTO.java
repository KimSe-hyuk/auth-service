package com.example.loginservice.dto;

import com.example.loginservice.enums.Role;
import com.example.loginservice.model.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
public class JoinRequestDTO {
    private String userId;
    private String userName;
    private String password;
    private String nickName;
    private String email;
    private Role role;
    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userId(userId)
                .userName(userName)
                .password(bCryptPasswordEncoder.encode(password))
                .nickName(nickName)
                .role(role)
                .email(email)
                .build();
    }
}
