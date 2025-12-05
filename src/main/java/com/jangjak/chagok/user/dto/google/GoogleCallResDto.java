package com.jangjak.chagok.user.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jangjak.chagok.user.dto.social.SocialCallbackDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoogleCallResDto implements SocialCallbackDto {

    private Long oauthId;

    private Long userId;

    @JsonProperty("isNew")
    private boolean isNew;

    private String email;

    private String name;

    private String picture;

    // 인터페이스 구현
    @Override
    public boolean isNew() {
        return this.isNew;
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

    @Override
    public String getProfileImage() {
        return this.picture;
    }
}