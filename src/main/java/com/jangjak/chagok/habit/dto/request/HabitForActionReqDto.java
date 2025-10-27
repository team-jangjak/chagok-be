package com.jangjak.chagok.habit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "액션 생성 요청을 위한 기본 정보")
public class HabitForActionReqDto {
    @Schema(description = "습관 ID", example = "1")
    private Long habitId;

//    @Schema(description = "습관 카테고리", example = "1")
//    private Long categoryId;

    @Schema(description = "습관", example = "공복 유산소 하기")
    private String habitTitle;

//    @Schema(description = "빈도 단위 (1=하루, 2=일주일, 3=한 달)", example = "2")
//    private Integer freqUnit; // 1=일, 2=주, 3=월

    @Schema(description = "빈도 수 (freq_unit 단위 안에서 몇 번 실행할지)", example = "3")
    private Integer frequency;

    @Schema(description = "인증 방식(사진/이미지 | 글 작성)", example = "이미지")
    private String verificationMethod;

    @Schema(description = "목표 기간", example = "8")
    private Integer weeksCount = 8;     // 기본 8주

    @Schema(description = "기타", example = "대학생에 알맞는 계획")
    private String etc;

}
