package com.jangjak.chagok.user.repository;

import com.jangjak.chagok.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthId(Long oauthId);

    boolean existsByEmail(String email);
}
