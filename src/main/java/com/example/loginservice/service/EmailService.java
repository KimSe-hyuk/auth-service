package com.example.loginservice.service;

import com.example.loginservice.dto.EmailVerify.EmailSearchDTO;
import com.example.loginservice.mapper.MemberMapper;
import com.example.loginservice.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final MemberMapper memberMapper;
    @Transactional
    public void insertMember(Member member) {
        // 이메일 중복 체크
        if (emailCheck(member.getUserName(),member.getEmailProvider())) {
            // 이메일이 이미 존재하면 아무 작업도 하지 않고 리턴
            System.out.println("이미 등록된 이메일입니다.");
        } else {
            // 이메일이 없으면 새로운 사용자 저장
            memberMapper.saveEmailUser(member);
        }
    }
    @Transactional(readOnly = true)
    public boolean emailCheck(String emailId,String emailProvider) {
        EmailSearchDTO build = EmailSearchDTO.builder()
                .emailProvider(emailProvider)
                .userName(emailId)
                .build();
        // 이메일이 존재하면 1 이상 반환, 없으면 0 반환
        return memberMapper.findEMailId(build) > 0;
    }

}
