package com.jangjak.chagok.habit.service.create.factory;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TemplateHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;

    @Override
    public HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime now) {
        Long habitId = reqDto.getTemplateHabitId();
        if  (habitId == null) throw new CustomException(ErrorCode.BAD_REQUEST);

        List<Action> actionList = habitQuery.getActionsByHabitId(now, habitId);

        // Action 개수 검증
        if (reqDto.getActions().size() != actionList.size()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return new HabitCreationInfo(habitId, actionList);
    }
}
