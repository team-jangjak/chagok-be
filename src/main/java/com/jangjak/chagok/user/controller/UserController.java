package com.jangjak.chagok.user.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.jwt.CookieUtil;
import com.jangjak.chagok.user.dto.ReissueResDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * user 회원가입
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

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @CookieValue(name = "access_token", required = false) String accessToken) {

        userService.logout(userInfo, accessToken);

        ResponseCookie delAccess = CookieUtil.deleteCookie("access_token", "/");
        ResponseCookie delRefresh = CookieUtil.deleteCookie("refresh_token", "/");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, delAccess.toString(), delRefresh.toString())
                .body(CommonResponse.ok(userInfo.getId(), "로그아웃이 완료되었습니다."));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        ReissueResDto result = userService.reissue(refreshToken);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, result.getAccessCookie().toString(), result.getRefreshCookie().toString())
                .body(CommonResponse.ok("토큰이 재발급되었습니다."));
    }

    @PostMapping("/test")
    public String test() {
        return "test";
    }
}
