package com.jangjak.chagok.user.service.socialLogin;

import com.jangjak.chagok.client.GoogleFeignClient;
import com.jangjak.chagok.client.GoogleTokenFeignClient;
import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.user.dto.google.GoogleCallResDto;
import com.jangjak.chagok.user.dto.google.GoogleDetailResDto;
import com.jangjak.chagok.user.dto.google.GoogleReqDto;
import com.jangjak.chagok.user.dto.google.GoogleResDto;
import com.jangjak.chagok.user.dto.social.OauthResDto;
import com.jangjak.chagok.user.dto.social.SocialUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleLoginService {

    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.client.pw}")
    private String googleClientPw;
    @Value("${google.login-uri}")
    private String googleApiUrl;
    @Value("${google.redirect-uri}")
    private String redirectUrl;

    private final GoogleFeignClient googleFeignClient;
    private final GoogleTokenFeignClient googleTokenFeignClient;
    private final SocialLoginService socialLoginService;

    //백엔드에서 구글 로그인 화면(authorization URL)을 생성해서 프론트에 전달해주는 메서드
    //사용자가 구글 로그인을 시도할 때 구글 인증 페이지로 리다이렉션할 수 있는 URL을 만들어 주는 역할
    public CommonResponse<String> getGoogleLoginView() {

        return CommonResponse.<String>builder()
                .data(googleApiUrl + "client_id=" + googleClientId
                        + "&redirect_uri=" + redirectUrl
                        + "&response_type=code"
                        + "&scope=email%20profile%20openid"
                        + "&access_type=offline")
                .message("google login view url입니다.")
                .build();
    }

    public GoogleDetailResDto loginGoogle(String code) {
        GoogleResDto googleTokenResponse = googleTokenFeignClient.getGoogleToken(GoogleReqDto.builder()
                .clientId(googleClientId)
                .clientSecret(googleClientPw)
                .code(code)
                .redirectUri("http://localhost:8080/user/google-login")
                .grantType("authorization_code")
                .build()).getBody();

        GoogleDetailResDto userInfo = googleFeignClient.getUserInfo("Bearer " + googleTokenResponse.getAccess_token()).getBody();
        return userInfo;
    }

    //응답받은 sub값으로 oauth db에 있는지 조회
    public GoogleCallResDto googleCallback(String code) {
        // 인가 코드에서 얻은 액세스토큰으로 사용자 정보 받기
        GoogleDetailResDto googleDetailResDto = loginGoogle(code);
        log.info("받은 googleDetailResDto 사용자 정보: {}", googleDetailResDto);
        SocialUserDto socialDto = SocialUserDto.builder()
                .socialId(googleDetailResDto.getSub())
                .provider("GOOGLE")
                .build();
        // 회원가입 or 로그인 처리
        OauthResDto oauthResDto = socialLoginService.findOrCreateSocialUser(socialDto);  //authId와 닉네임
        log.info("oauthResDto: {}", oauthResDto);

        return GoogleCallResDto.builder()
                .oauthId(oauthResDto.getOauthId())
                .userId(oauthResDto.getUserId())
                .name(googleDetailResDto.getName())
                .email(googleDetailResDto.getEmail())
                .picture(googleDetailResDto.getPicture())
                .isNew(oauthResDto.isNewUser())
                .build();
    }

}
