package com.example.loginservice.controller;

import com.example.loginservice.dto.login.JoinRequestDTO;
import com.example.loginservice.dto.login.LoginRequestDTO;
import com.example.loginservice.dto.login.SignInResponseDTO;
import com.example.loginservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/auth/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @PostMapping("/login")
    public SignInResponseDTO registerUser(@RequestBody LoginRequestDTO userRegistrationDTO) {
        SignInResponseDTO signInResponseDTO = memberService.signIn(userRegistrationDTO.getUserId(), userRegistrationDTO.getPassword());
        System.out.println(signInResponseDTO.isLoggedIn());
        return signInResponseDTO;
    }
    @PostMapping("/join")
    public ResponseEntity<String> joinUser(@RequestBody JoinRequestDTO joinRequestDTO) {
        memberService.signUp(joinRequestDTO.toMember(bCryptPasswordEncoder));
        // 사용자 등록 로직
        return ResponseEntity.ok("User registered successfully");
    }
    //트루가 리턴시 사용이 가능
    @PostMapping("/check-id")
    public boolean checkId(@RequestBody String userId){
        return  memberService.checkId(userId);
    }
    @PostMapping("/check-nickName")
    public boolean checkNickName(@RequestBody String nickName){
        return memberService.checkNickName(nickName);
    }

}
