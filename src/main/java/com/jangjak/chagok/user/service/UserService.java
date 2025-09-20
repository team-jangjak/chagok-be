package com.jangjak.chagok.user.service;

import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.jwt.CookieUtil;
import com.jangjak.chagok.common.jwt.JwtTokenProvider;
import com.jangjak.chagok.user.dto.ReissueResDto;
import com.jangjak.chagok.user.dto.UserReqDto;
import com.jangjak.chagok.user.dto.UserResDto;
import com.jangjak.chagok.user.entity.User;
import com.jangjak.chagok.user.repository.OauthRepository;
import com.jangjak.chagok.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static com.jangjak.chagok.common.jwt.JwtTokenProvider.getRefreshTokenKey;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;
    private final RedisTemplate<String, String> redisStringTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.secret-key}")
    private String secretKey;


    /**
     * user 회원가입
     */
    public Long userCreate(@Valid UserReqDto userReqDto) {
        userRepository.findByOauthId(userReqDto.getOauthId()).ifPresent(user -> {
            throw new CustomException("이미 가입된 사용자입니다.", HttpStatus.BAD_REQUEST);
        });

        if (oauthRepository.findById(userReqDto.getOauthId()).isEmpty()) {
            throw new CustomException("존재하지 않는 oauth", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .oauthId(userReqDto.getOauthId())
                .name(userReqDto.getName())
                .email(userReqDto.getEmail())
                .profileImage(userReqDto.getProfileImage())
                .birthDate(userReqDto.getBirthDate())
                .tendency(userReqDto.getTendency())
                .build();

        userRepository.save(user);
        return user.getId();

    }

    /**
     * 로그아웃
     */
    public void logout(TokenUserInfo userInfo, String accessToken) {
        redisStringTemplate.delete(getRefreshTokenKey() + userInfo.getId().toString());
        jwtTokenProvider.logout(accessToken, userInfo.getId());
        log.info("{}번 사용자의 로그아웃 성공", userInfo.getId());
    }

    /**
     * 토큰 재발급
     */
    public ReissueResDto reissue(String refreshToken) {
        // 리프레시 토큰 검증 (서명 검증)
        Claims claims;
        try {
            claims = jwtTokenProvider.getClaims(refreshToken, secretKey);
        } catch (Exception e) {
            throw new CustomException("리프레시 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
        Long userId = Long.valueOf(claims.getSubject());

        // Redis 저장값과 일치 검증
        String key = getRefreshTokenKey() + userId;
        String stored = redisStringTemplate.opsForValue().get(key);
        if (stored == null || !stored.equals(refreshToken)) {
            throw new CustomException("저장된 리프레시 토큰과 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        // 기존 refresh 폐기 -> 새 토큰 발급
        redisStringTemplate.delete(key);

        String newAccess = jwtTokenProvider.generateAccessToken(userId);
        String newRefresh = jwtTokenProvider.generateRefreshToken(userId);

        redisStringTemplate.opsForValue().set(key, newRefresh);

        // 쿠키 생성
        ResponseCookie accessCookie =
                CookieUtil.httpOnlyCookie("access_token", newAccess, 60L * 60, "/");
        ResponseCookie refreshCookie =
                CookieUtil.httpOnlyCookie("refresh_token", newRefresh, 60L * 60 * 24 * 7, "/");

        log.info("토큰 재발급 완료");
        return new ReissueResDto(accessCookie, refreshCookie);
    }

    public UserResDto getInfo(TokenUserInfo userInfo) {
        User user = userRepository.findById(userInfo.getId()).orElseThrow(() ->
                new CustomException("해당하는 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        return UserResDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .profileImage(user.getProfileImage())
                .tendency(user.getTendency())
                .build();
    }
}
