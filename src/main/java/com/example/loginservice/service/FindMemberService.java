package com.example.loginservice.service;

import com.example.loginservice.dto.find.FindEmailRequestDTO;
import com.example.loginservice.dto.find.FindUserIdEmailRequestDTO;
import com.example.loginservice.mapper.MemberMapper;
import com.example.loginservice.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindMemberService {
    private final MemberMapper memberMapper;
    @Transactional(readOnly = true)
    public boolean checkEmail(String email) {
        return 0<memberMapper.findEmail(email);
    }
    @Transactional(readOnly = true)
    public String getUserId(FindEmailRequestDTO email) {
        return memberMapper.getUserId(email.getEmail());
    }
    @Transactional
    public boolean resetPw(Member member) {
        System.out.println("🔍 updatePassword 실행 - userId: " + member.getUserId());
        System.out.println("🔍 updatePassword 실행 - password: " + member.getPassword());
        int result = memberMapper.updatePassword(member);
        System.out.println("🔍 updatePassword 결과: " + result);
        return result > 0;
    }

    @Transactional(readOnly = true)
    public boolean checkEmailUserId(FindUserIdEmailRequestDTO userIdEmailRequestDto) {
        return 0<memberMapper.findEmailId(userIdEmailRequestDto);
    }
}
