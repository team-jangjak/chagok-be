package com.jangjak.chagok.user.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long oauthId;

    @Column(nullable = false)
    private String name;

    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @Column(nullable = false)
    private LocalDate birthDate;

    @Setter
    @Column(nullable = false)
    private String profileImage;

    @Setter
    @Column(nullable = false)
    private Integer tendency;

}
