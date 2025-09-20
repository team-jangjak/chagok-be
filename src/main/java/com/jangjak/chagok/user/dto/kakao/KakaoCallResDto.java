package com.jangjak.chagok.user.dto.kakao;

import com.jangjak.chagok.user.dto.social.SocialCallbackDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoCallResDto implements SocialCallbackDto {
    @Schema(description = "OAuth ID", example = "1234567890")
    private Long oauthId;

    @Schema(description = "user ID", example = "1")
    private Long userId;

    @Schema(description = "신규 가입자 여부", example = "true")
    private boolean newUser;

    @Schema(description = "사용자 이름", example = "abcd")
    private String name;

    @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/dn/...")
    private String profileImage;

    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;

    // 인터페이스 메서드 구현
    @Override
    public boolean isNew() {
        return this.newUser;
    }

    @Override
    public Long getOauthId() {
        return this.oauthId;
    }

    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }
}
