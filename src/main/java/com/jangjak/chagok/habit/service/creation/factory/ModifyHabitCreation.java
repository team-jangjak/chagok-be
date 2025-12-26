package com.jangjak.chagok.habit.service.creation.factory;

import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.mapper.ActionMapper;
import com.jangjak.chagok.habit.mapper.HabitMapper;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModifyHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;

   @Override
    public HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime validStDt) {
        // 템플릿 습관 조회
        Habit templateHabit = habitQuery.getHabitById(reqDto.getTemplateHabitId());

        // 습관 생성
        Habit habit = HabitMapper.toEntity(reqDto, templateHabit, validStDt);
        Long habitId = habitQuery.saveHabit(habit).getId().getHabitId();

        // 행위 생성
        List<Action> actions = ActionMapper.toEntities(habitId, reqDto, validStDt);
        habitQuery.saveActionList(actions);

        return new HabitCreationInfo(habitId, actions);
    }
}
