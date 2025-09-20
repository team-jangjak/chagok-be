package com.jangjak.chagok.common.jwt;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {

    public static ResponseCookie httpOnlyCookie(String name, String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)           // 로컬 개발: false, 운영: true(HTTPS 필수)
                .sameSite("Lax")      // 필요에 따라 Lax/None(크로스 도메인) 조정
                .path(path)
                .maxAge(maxAgeSeconds)
                .build();
    }

    public static ResponseCookie deleteCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(path)
                .maxAge(0)
                .build();
    }
}