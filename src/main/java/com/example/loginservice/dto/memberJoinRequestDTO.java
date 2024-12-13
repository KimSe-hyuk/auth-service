package com.example.loginservice.dto;


import com.example.loginservice.model.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class memberJoinRequestDTO {
    private String email;
    private String password;
    private String name;
    private String nickName;
    private String email_provider;
    public Member toMember(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Member.builder()
                .userId(email)
                .password(bCryptPasswordEncoder.encode(password))
                .userName(name)
                .nickName(nickName)
                .emailProvider(email_provider)
                .build();
    }
}
