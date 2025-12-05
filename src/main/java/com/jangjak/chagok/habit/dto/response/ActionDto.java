package com.jangjak.chagok.habit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "AI가 생성한 습관 계획 데이터 모델")
public class ActionDto {

    @Schema(description = "주차별 계획 목록")
    private List<WeekPlan> weeks;

    @Data
    @Schema(description = "한 주차의 계획 세부 정보")
    public static class WeekPlan {

        @Schema(description = "주차 (1부터 시작)", example = "1")
        private Integer week;

        @Schema(description = "주간 실행 횟수 (1~7)", example = "3")
        private Integer freq;

        @Schema(description = "실행할 세부 task 목록", example = "[\"아침 러닝 30분\", \"점심 산책 20분\"]")
        private List<String> tasks;

        @Schema(description = "해당 주차 계획 이유", example = "주중 피로를 고려해 주말 위주로 구성")
        private String reason;
    }
}