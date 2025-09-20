package com.jangjak.chagok.user.dto.social;


public interface SocialCallbackDto {
    boolean isNew();

    Long getOauthId();

    String getName();

    String getEmail();

    Long getUserId();
}