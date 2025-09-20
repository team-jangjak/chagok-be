package com.jangjak.chagok.user.repository;

import com.jangjak.chagok.user.entity.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthRepository extends JpaRepository<Oauth, Long> {
    Optional<Oauth> findBySocialProviderAndSocialId(String socialId, String socialProvider);
}
