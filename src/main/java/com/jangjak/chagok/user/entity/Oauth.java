package com.jangjak.chagok.user.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
@Table(
  name = "oauth",
  uniqueConstraints = @UniqueConstraint(
    name = "uk_oauth_provider_socialid",
    columnNames = {"social_provider", "social_id"}
  )
)
public class Oauth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oauthId;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String socialProvider;
}
