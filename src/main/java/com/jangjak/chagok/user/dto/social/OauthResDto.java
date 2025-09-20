package com.jangjak.chagok.user.dto.social;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OauthResDto {
    private Long oauthId;
    private Long userId;
    private boolean newUser;
    private String socialId;
}