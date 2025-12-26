package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateActionRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserAction;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.mapper.UserActionMapper;
import com.jangjak.chagok.habit.mapper.UserHabitMapper;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.habit.util.DateValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TemplateHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;

    @Override
    public boolean validateRequest(Long userId, CreateHabitRequestDto reqDto) {
        TemplateHabitRequestDto request = convertRequest(reqDto);

        // 템플릿 습관이 실제 존재하는 지 확인
        if (!habitQuery.isTemplateHabit(request.getHabitId())) return false;

        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        List<TemplateActionRequestDto> actions = request.getActions();

        return DateValidationUtil.validateAndParseHabitDates(start, end)
                && !actions.isEmpty();
    }

    @Override
    public boolean validateRequest(Long userId, HabitCreateRequestDto reqDto) {
        return false;
    }

    public HabitCreationInfo createHabit(CreateHabitRequestDto reqDto) {
        TemplateHabitRequestDto request = convertRequest(reqDto);

        Long habitId = request.getHabitId();
        List<Action> actionList = habitQuery.getActionsByHabitId(habitId);

        // Action 개수 검증
        if (request.getActions().size() != actionList.size()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return new HabitCreationInfo(habitId, actionList);
    }

    @Override
    public HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime now) {
        return null;
    }

    private TemplateHabitRequestDto convertRequest(CreateHabitRequestDto reqDto) {
        if (!(reqDto instanceof TemplateHabitRequestDto)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return (TemplateHabitRequestDto) reqDto;
    }
}
