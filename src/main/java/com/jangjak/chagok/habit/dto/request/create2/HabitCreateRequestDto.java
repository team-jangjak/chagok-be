package com.jangjak.chagok.habit.dto.request.create2;

import com.jangjak.chagok.common.anotation.DateFormatter;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.List;

@Data
public class HabitCreateRequestDto {
    @Range(min = 0, max = 2)
    private Integer requestType;        // 0 : 새로운 습관 생성, 1 : 템플릿 습관 생성, 2 : 템플릿 수정 습관 생성

    private String habitTitle;          // 습관 이름
    private Long habitCategory;         // 습관 카테고리
    private String habitImage;          // 습관 이미지

    @DateFormatter
    private LocalDate habitStartDate;   // 습관 시작일
    @DateFormatter
    private LocalDate habitEndDate;     // 습관 종료일

    private Integer frequency;          // 습관 빈도 단위 당 행위 횟수
    private Integer freqUnit;           // 습관 빈도 단위 (일/주/월)
    private Integer freqCount;          // 습관 기간 내 총 습관 빈도 단위 수

    private String templateHabitId;     // 습관 템플릿 ID (새로 만드는 습관의 경우 null 값 요청)
    private Boolean isPublic;           // 습관 공개 유무

    private List<ActionCreateRequestDto> actions; // 행위 리스트
}
