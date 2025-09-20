package com.jangjak.chagok.user.dto;

import lombok.*;
import org.springframework.http.ResponseCookie;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResDto {
    Long id;
    ResponseCookie accessCookie;
    ResponseCookie refreshCookie;
}