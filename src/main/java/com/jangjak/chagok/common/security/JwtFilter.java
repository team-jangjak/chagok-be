package com.jangjak.chagok.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_BLACKLIST_KEY = "BLACKLIST_ACCESS_TOKEN:";

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    List<String> whiteList = List.of(
            "/user/sign-up", "/user/kakao-login", "/user/google-login", "/user/google-login-view", "/user/reissue",
            "/habit/popular-habit-category"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Path 점검
        String path = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        log.info("{} 요청이 발생했습니다.", path);

        // 허용 url 리스트를 순회하면서 지금 들어온 요청 url과 하나라도 일치하면 true 리턴
        boolean isAllowed = whiteList.stream()
                .anyMatch(url -> antPathMatcher.match(url, path));

        // 허용 path라면 Filter 동작하지 않고 넘기기
        if (isAllowed || path.contains("swagger") || path.contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 동작
        try {
            // 쿠키에서 access_token 읽기
            String token = getCookieValue(request);
            if (token == null || token.isEmpty()) {
                log.warn("access_token 쿠키가 없습니다.");
                throw new Exception("NO_TOKEN");
            }

            // 토큰에서 사용자 정보 추출
            Claims claims = getClaims(token);
            log.info("claims: {}", claims);

            // 토큰에서 정보 추출
            long id = Long.parseLong(claims.getSubject());

            // 블랙리스트 검사 로직
            // Redis에서 현재 토큰이 블랙리스트에 등록되어 있는지 확인
            if (token.equals(redisTemplate.opsForValue().get(ACCESS_TOKEN_BLACKLIST_KEY + id))) {
                log.warn("블랙리스트에 등록된 토큰으로 접근 시도. 토큰: {}", token);
                onError(response, 401, "블랙리스트에 등록된 토큰입니다.");
                return;
            }

            if (!validateToken(token)) {
                log.warn("토큰이 만료되었습니다.");
                throw new Exception();
            }

            Role role = Role.from(claims.get("role", String.class));

            // @AuthenticationPrinciple, @PreAuthorize("hasRole('ADMIN')") 같은 로직을 사용하기 위한 로직
            TokenUserInfo tokenUserInfo = TokenUserInfo.builder().id(id).role(role.name()).build();

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenUserInfo, "", authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            log.warn("토큰 정보가 유효하지 않습니다.");
            onError(response, 666, "토큰 검증에 실패하였습니다.");
            return;
        }

        log.info("필터 진행");
        // 문제 없다면 진행
        filterChain.doFilter(request, response);
    }

    /**
     * claim 꺼내기
     *
     * @param token
     * @return
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("토큰에서 Claim을 꺼내는 과정에서 오류가 발생했습니다.");
            throw e;
        }
    }

    /**
     * 토큰 유효기간 검증
     *
     * @param token
     * @return
     */
    private boolean validateToken(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("토큰 검증 과정에서 문제가 발생했습니다.");
            throw e;
        }
    }

    /**
     * 인증 통과하지 못하면(토큰에 문제가 있다면) 에러 응답 전송
     *
     * @param response
     * @throws IOException
     */
    private void onError(HttpServletResponse response, int httpStatus, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 공통 실패 응답 JSON으로 변환
        String body = objectMapper.writeValueAsString(CommonResponse.fail(httpStatus, message));
        response.getWriter().write(body);
    }

    /**
     * 쿠키 꺼내기
     */
    private String getCookieValue(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if ("access_token".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

}
