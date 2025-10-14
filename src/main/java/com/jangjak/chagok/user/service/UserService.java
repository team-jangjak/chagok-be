package com.jangjak.chagok.user.service;

import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.common.jwt.CookieUtil;
import com.jangjak.chagok.common.jwt.JwtTokenProvider;
import com.jangjak.chagok.user.dto.ReissueResDto;
import com.jangjak.chagok.user.dto.UserCookieResDto;
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
    @Value("${jwt.expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    /**
     * user 회원가입
     */
    public Long userCreate(@Valid UserReqDto userReqDto) {
        userRepository.findByOauthId(userReqDto.getOauthId()).ifPresent(user -> {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        });

        if (oauthRepository.findById(userReqDto.getOauthId()).isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
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
    public UserCookieResDto logout(TokenUserInfo userInfo, String accessToken) {
        redisStringTemplate.delete(getRefreshTokenKey() + userInfo.getId().toString());
        jwtTokenProvider.logout(accessToken, userInfo.getId());
        ResponseCookie delAccess = CookieUtil.deleteCookie("access_token", "/");
        ResponseCookie delRefresh = CookieUtil.deleteCookie("refresh_token", "/");

        log.info("{}번 사용자의 로그아웃 성공", userInfo.getId());
        return new UserCookieResDto(delAccess, delRefresh);
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
                CookieUtil.httpOnlyCookie("access_token", newAccess, accessTokenExpiration, "/");
        ResponseCookie refreshCookie =
                CookieUtil.httpOnlyCookie("refresh_token", newRefresh, refreshTokenExpiration, "/");

        log.info("토큰 재발급 완료");
        return new ReissueResDto(accessCookie, refreshCookie);
    }

    /**
     * 회원 정보 조회
     */
    public UserResDto getInfo(TokenUserInfo userInfo) {
        User user = userRepository.findById(userInfo.getId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND));

        return UserResDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .profileImage(user.getProfileImage())
                .tendency(user.getTendency())
                .build();
    }

    public void emailCheck(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }
}
