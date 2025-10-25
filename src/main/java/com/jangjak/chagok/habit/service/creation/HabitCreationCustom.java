package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.request.create.NewActionRequestDto;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HabitCreationCustom extends HabitCreationBase{
    private final HabitQuery habitQuery;

    @Override
    public HabitCreationInfo createHabit(CustomHabitRequestDto reqDto) {
        // === 2-2: 새로운 습관 생성 케이스 ===
        // Category 존재 여부 검증
        habitQuery.getHabitCategoryById(reqDto.getCategoryId());

        // 요청 데이터를 기반으로 새로운 Habit 엔티티 생성
        Habit rawHabit = Habit.builder()
                .title(reqDto.getTitle())                    // 습관 제목
                .categoryId(reqDto.getCategoryId())          // 카테고리 ID
                .frequency(reqDto.getFrequency())            // 빈도 (횟수)
                .freqUnit(reqDto.getFrequencyUnit())         // 빈도 단위 (일/주/월)
                .isPublic(reqDto.getIsPublic() ? YN.Y : YN.N)  // 공개 여부 (Boolean → String 변환)
                .build();

        // 생성된 습관을 DB에 저장하고 생성된 ID 반환
        Long habitId = habitQuery.saveHabit(rawHabit).getId();

        // 요청에 포함된 액션 목록을 Action 엔티티로 변환
        List<NewActionRequestDto> actionReqList = reqDto.getActions();
        List<Action> actions = actionReqList.stream()
                .map(value -> Action.builder()
                        .habitId(habitId)                    // 방금 생성된 습관 ID 연결
                        .sequence(value.getSequence())       // 액션 순서
                        .content(value.getContent())         // 액션 내용
                        .freqSeq(value.getFreqSeq())         // 빈도 순서
//                        .checkMethod(value.getCheckMethod()) // 체크 방법
                        .build()
                )
                .toList();

        // Action DB 저장
        habitQuery.saveActionList(actions);

        return new HabitCreationInfo(habitId, actions);
    }
}
