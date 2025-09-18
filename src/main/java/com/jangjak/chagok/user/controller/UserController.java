package com.jangjak.chagok.user.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.user.dto.UserReqDto;
import com.jangjak.chagok.user.dto.google.GoogleCallResDto;
import com.jangjak.chagok.user.dto.kakao.KakaoCallResDto;
import com.jangjak.chagok.user.service.UserService;
import com.jangjak.chagok.user.service.socialLogin.GoogleLoginService;
import com.jangjak.chagok.user.service.socialLogin.KakaoLoginService;
import com.jangjak.chagok.user.service.socialLogin.SocialLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final GoogleLoginService googleLoginService;
    private final SocialLoginService socialLoginService;
    private final KakaoLoginService kakaoLoginService;

    /**
     * 소셜 user 회원가입
     */
    @PostMapping("/sign-up")
    public ResponseEntity<?> createSocialAuth(@Valid @RequestBody UserReqDto userReqDto) {
        Long userId = userService.userCreate(userReqDto);
        return ResponseEntity.ok().body(CommonResponse.ok(userId, "회원가입이 완료되었습니다."));
    }

    /**
     * 구글 로그인/회원가입
     */
    @GetMapping("/google-login-view")
    public ResponseEntity<?> getGoogleLoginView() {
        return ResponseEntity.ok(googleLoginService.getGoogleLoginView());
    }

    @GetMapping("/google-login")
    public void googleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        GoogleCallResDto googleCallResDto = googleLoginService.googleCallback(code);
        socialLoginService.writeSocialLoginResponse(googleCallResDto, "GOOGLE", response);
    }

    /**
     * 카카오 로그인/회원가입
     */
    // 카카오 콜백 요청 처리
    @GetMapping("/kakao-login")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        KakaoCallResDto kakaoCallResDto = kakaoLoginService.kakaoCallback(code);
        socialLoginService.writeSocialLoginResponse(kakaoCallResDto, "KAKAO", response);
    }

    @PostMapping("/test")
    public String test(){
        return "test";
    }
}
