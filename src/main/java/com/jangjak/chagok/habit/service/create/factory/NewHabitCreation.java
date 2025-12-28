package com.jangjak.chagok.habit.service.create.factory;

import com.jangjak.chagok.external.service.s3.S3ImageService;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.*;
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
public class NewHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;
    private final S3ImageService s3ImageService;

    @Override
    public HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime validStDt) {
        // 이미지 등록
        String imageUrl = s3ImageService.registerImage(reqDto.getHabitImage());
        reqDto.setHabitImage(imageUrl);

        // 습관 생성
        Habit habit = HabitMapper.toEntity(reqDto, validStDt);
        Long habitId = habitQuery.saveHabit(habit).getId().getHabitId();

        // 행위 생성
        List<Action> actions = ActionMapper.toEntities(habitId, reqDto, validStDt);
        habitQuery.saveActionList(actions);

        return new HabitCreationInfo(habitId, actions);
    }
}
