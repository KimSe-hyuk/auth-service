package com.example.loginservice.service;

import com.example.loginservice.config.security.CustomUserDetails;
import com.example.loginservice.mapper.MemberMapper;
import com.example.loginservice.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final MemberMapper memberMapper;
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        System.out.println("사용자 인증 시작: " + userId);

        Member member = memberMapper.findUserByUserId(userId);
        if (member == null) {
            System.out.println("❌ member is null for username: " + userId);
            throw new UsernameNotFoundException(userId + " not found");
        }

        System.out.println("🔍 DB에서 가져온 비밀번호 해시: " + member.getPassword());

        return CustomUserDetails.builder()
                .member(member)
                .roles(List.of(String.valueOf(member.getRole())))
                .build();
    }

}
