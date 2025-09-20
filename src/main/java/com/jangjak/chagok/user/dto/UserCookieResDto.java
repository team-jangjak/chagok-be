package com.jangjak.chagok.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class UserCookieResDto {
    ResponseCookie delAccess;
    ResponseCookie delRefresh;
}
