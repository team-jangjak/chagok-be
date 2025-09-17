package com.jangjak.chagok.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResDto {
    Long id;
    String accessToken;
    String refreshToken;
    boolean recoveryTarget;
}