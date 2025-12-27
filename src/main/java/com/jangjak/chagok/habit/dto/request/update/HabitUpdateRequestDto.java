package com.jangjak.chagok.habit.dto.request.update;

import com.jangjak.chagok.habit.dto.request.create2.ActionCreateRequestDto;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class HabitUpdateRequestDto {

    @NotNull
    private Long id; // 변경할 습관의 ID

    private String habitTitle;          // 습관 이름
    private Long habitCategory;         // 습관 카테고리
    private String habitImage;          // 습관 이미지

    private Integer frequency;          // 습관 빈도 단위 당 행위 횟수
    private Integer freqUnit;           // 습관 빈도 단위 (일/주/월)
    private Integer freqCount;          // 습관 기간 내 총 습관 빈도 단위 수

    private Boolean isTemplate;         // 습관 템플릿 공개 유무

    private List<ActionCreateRequestDto> actions; // 행위 리스트
}
