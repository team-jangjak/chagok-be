package com.jangjak.chagok.user.dto;

import com.jangjak.chagok.user.enums.GENDER;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReqDto {
    @Schema(description = "OAuth Id", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "OauthId는 필수 입니다.")
    private Long oauthId;

    @Schema(description = "이름", example = "차곡유저", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수 입니다.")
    private String name;

    @Schema(description = "이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수 입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "생년월일(ISO-8601, yyyy-MM-dd)", example = "2004-03-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "생년월일은 필수 입니다.")
    private LocalDate birthDate;

    @Schema(description = "프로필 이미지 URL", example = "https://cdn.chagok.shop/avatars/u42.png", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "프로필 이미지 url은 필수 입니다.")
    private String profileImage;

    @Schema(description = "성향 점수(0~100 가정)", example = "73", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "성향 점수는 필수 입니다.")
    private Integer tendency;

    @Schema(description = "성별", example = "M", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "성별은 필수 입니다.")
    private String gender;
}
