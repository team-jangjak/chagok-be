package com.jangjak.chagok.habit.dto.request;

import com.jangjak.chagok.habit.enums.HabitCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "AI에게 요청할 습관에 대한 정보")
public class ActionToAIReqDto {

    @Schema(description = "습관 타이틀", example = "독서하기")
    private String habitTitle;

    @Schema(description = "습관 카테고리", example = "운동")
    private HabitCategory categoryName;

//    @Schema(description = "빈도 단위 (1=하루, 2=일주일, 3=한 달)", example = "2")
//    private Integer freqUnit; // 1=일, 2=주, 3=월

    @Schema(description = "빈도 수 (1주일 동안 몇 번 실행할지)", example = "3")
    private Integer frequency;

    @Schema(description = "성향 점수(0~100)", example = "42")
    private Integer tendency;

    @Schema(description = "나이", example = "21")
    private Integer age;

    @Schema(description = "기타", example = "직장인 맞춤형 계획")
    private String etc;

    @Schema(description = "인증 방식(사진/이미지 | 글 작성)", example = "글 작성")
    private String verificationMethod;

    @Schema(description = "목표 기간", example = "8주")
    private Integer weeksCount = 8;     // 기본 8주

    @Schema(description = "사용할 모델", example = "gpt-4.1-mini", defaultValue = "gpt-4.1-mini")
    private String model = "gpt-4.1-mini";
}
