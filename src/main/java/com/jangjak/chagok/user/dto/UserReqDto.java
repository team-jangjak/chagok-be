package com.jangjak.chagok.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReqDto {
    private Long oauthId;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String profileImage;
    private Integer tendency;
}
