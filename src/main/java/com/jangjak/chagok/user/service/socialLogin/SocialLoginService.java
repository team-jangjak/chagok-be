package com.jangjak.chagok.user.service.socialLogin;


import com.jangjak.chagok.common.jwt.JwtTokenProvider;
import com.jangjak.chagok.user.dto.UserLoginResDto;
import com.jangjak.chagok.user.dto.social.OauthResDto;
import com.jangjak.chagok.user.dto.social.SocialCallbackDto;
import com.jangjak.chagok.user.dto.social.SocialUserDto;
import com.jangjak.chagok.user.entity.Oauth;
import com.jangjak.chagok.user.entity.User;
import com.jangjak.chagok.user.repository.OauthRepository;
import com.jangjak.chagok.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialLoginService {

    private final OauthRepository oauthRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Transactional
    public OauthResDto findOrCreateSocialUser(SocialUserDto dto) {
        // 기존 소셜 로그인 사용자인지 socialId로 기존 사용자 찾기
        Optional<Oauth> oauth = oauthRepository.findBySocialProviderAndSocialId(dto.getProvider(), dto.getSocialId());

        log.info("dto.getId(): {} oauth가 있다면: {}", dto.getSocialId(), oauth);

        // 기존 사용자 존재
        if (oauth.isPresent()) {
            log.info("기존에 소셜 로그인한 유저입니다.");
            Oauth foundUser = oauth.get();

            Optional<User> byOauthId = userRepository.findByOauthId(oauth.get().getId());
            //byOauthId가 null이면
            if (byOauthId.isEmpty()) {
                log.info("oauth는 생성하였지만 회원가입을 마치지 못한 유저입니다.");
                return OauthResDto.builder()
                        .oauthId(foundUser.getId())
                        .socialId(foundUser.getSocialId())
                        .newUser(true)
                        .build();
            }

            log.info("byOauthId: {}", byOauthId);
            return OauthResDto.builder()
                    .oauthId(foundUser.getId())
                    .socialId(foundUser.getSocialId())
                    .userId(byOauthId.get().getId())
                    .newUser(false)
                    .build();
        }

        // 처음 소셜 로그인 한 사람 -> 새 사용자 생성. oauth
        log.info("소셜 로그인으로 처음 방문한 신규 유저입니다. 회원가입 진행해야 됨");

        Oauth build = Oauth.builder()
                .socialProvider(dto.getProvider())
                .socialId(dto.getSocialId().toString())
                .build();

        Oauth saved = oauthRepository.save(build);

        log.info("saved: {}", saved);

        return OauthResDto.builder()
                .oauthId(saved.getId())
                .newUser(true)
                .build();
    }

    private UserLoginResDto generateAuthLoginResDto(Long userId) {
        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        // 토큰 저장
        jwtTokenProvider.saveRefreshToken(userId, refreshToken);

        log.info("로그인 성공 : {}", userId);

        return UserLoginResDto.builder()
                .id(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .recoveryTarget(false)
                .build();
    }


    public void writeSocialLoginResponse(SocialCallbackDto res, String provider, HttpServletResponse response) throws IOException {
        String html;
        if (!res.isNew()) {
            UserLoginResDto loginDto = generateAuthLoginResDto(res.getUserId());
            log.info("loginDto: {}", loginDto);
            html = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head><title>%s 로그인 완료</title></head>
                            <body>
                                <script>
                                    if (window.opener) {
                                        window.opener.postMessage({
                                            type: 'OAUTH_SUCCESS',
                                            accessToken: '%s',
                                            refreshToken: '%s',
                                            id: '%s',
                                            recoveryTarget: '%s',
                                            provider: '%s'
                                        }, 'http://localhost:5173');
                                        window.close();
                                    } else {
                                        window.location.href = 'http://localhost:5173';
                                    }
                                </script>
                                <p>%s 로그인 처리 중...</p>
                            </body>
                            </html>
                            """, provider, loginDto.getAccessToken(), loginDto.getRefreshToken(), loginDto.getId(),
                    loginDto.isRecoveryTarget(), provider, provider);
        } else {
            String encodedNickname = URLEncoder.encode(res.getName(), StandardCharsets.UTF_8);
            String encodedEmail = URLEncoder.encode(res.getEmail(), StandardCharsets.UTF_8);
            String redirectUrl = String.format(
                    "http://localhost:5173/auth/signup/social-user-signup?provider=%s&oauthId=%s&nickname=%s&email=%s",
                    provider, res.getOauthId(), encodedNickname, encodedEmail
            );
            html = String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head><title>회원가입</title></head>
                    <body>
                        <script>
                            if (window.opener) {
                                window.opener.postMessage({
                                    type: 'NEW_USER_SIGNUP',
                                    oauthId: '%s',
                                    provider: '%s'
                                }, 'http://localhost:5173');
                                window.close();
                            } else {
                                window.location.href = '%s';
                            }
                        </script>
                        <p>회원가입 페이지로 이동 중...</p>
                    </body>
                    </html>
                    """, res.getOauthId(), provider, redirectUrl);
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(html);
    }

}
