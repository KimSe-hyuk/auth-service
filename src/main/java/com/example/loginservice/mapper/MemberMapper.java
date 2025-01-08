package com.example.loginservice.mapper;

import com.example.loginservice.dto.EmailVerify.EmailSearchDTO;
import com.example.loginservice.dto.find.FindUserIdEmailRequestDTO;
import com.example.loginservice.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    Member findUserByUserId(String userId);
    void saveUser(Member user);
    void saveEmailUser(Member email);
    int findEMailId(EmailSearchDTO email);
    int checkUserIdExist(String userId);
    int checkNickNameExist(String nickName);
    int findEmail(String email);
    int findEmailId(FindUserIdEmailRequestDTO emailUserId);
    int updatePassword(Member member);
    String getUserId(String email);

    Member memberDetail(String userId);
}
