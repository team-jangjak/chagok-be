package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModifyHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;

    @Override
    public HabitCreationInfo createHabit(CreateHabitRequestDto reqDto) {
        // === 2-1: 기존 습관 재활용 케이스 ===

//        Long habitId = reqDto.getHabitId();
//
//        // 해당 ID의 습관이 실제로 존재하는지 확인
//        // 존재하지 않으면 CustomException(NOT_FOUND) 발생
//        habitQuery.getHabitById(habitId);
//
//        // 해당 습관에 연결된 모든 액션 조회
//        List<Action> actions = habitQuery.getActionsByHabitId(habitId);
//
//        // 액션이 하나도 없는 경우는 데이터 무결성 문제이므로 예외 발생
//        if (actions.isEmpty()) {
//            throw new CustomException(ErrorCode.DATASET_ERROR);
//        }
//
//        return new HabitCreationInfo(habitId, actions);
        return null;
    }

    @Override
    public Long createUserHabit(Long userId, CreateHabitRequestDto reqDto, HabitCreationInfo habitInfo) {
        // TODO : 액션은 날짜 순서대로 넣어야 함.
        return null;
    }

    @Override
    public boolean validateRequest(Long userId, CreateHabitRequestDto reqDto) {
        return false;
    }

    private ModifyHabitRequestDto convertRequest(CreateHabitRequestDto reqDto) {
        return reqDto instanceof ModifyHabitRequestDto ? (ModifyHabitRequestDto) reqDto : null;
    }
}
