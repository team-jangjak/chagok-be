package com.jangjak.chagok.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReqDto {
    @NotBlank(message = "OauthId는 필수 입니다.")
    private Long oauthId;
    @NotBlank(message = "이름은 필수 입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수 입니다.")
    private String email;
    @NotBlank(message = "생년월일은 필수 입니다.")
    private LocalDate birthDate;
    @NotBlank(message = "프로필 이미지 url은 필수 입니다.")
    private String profileImage;
    @NotBlank(message = "성향 점수는 필수 입니다.")
    private Integer tendency;
}
