package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.*;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.mapper.ActionMapper;
import com.jangjak.chagok.habit.mapper.HabitMapper;
import com.jangjak.chagok.habit.mapper.UserActionMapper;
import com.jangjak.chagok.habit.mapper.UserHabitMapper;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.habit.util.DateValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;

    @Override
    public boolean validateRequest(Long userId, CreateHabitRequestDto reqDto) {
        NewHabitRequestDto request = convertRequest(reqDto);

        // 날짜 검증
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        if (!DateValidationUtil.validateAndParseHabitDates(start, end)) return false;

        // action 검증
        List<NewActionRequestDto> actions = request.getActions();
        if (actions == null || actions.isEmpty()) return false;

        // 인증방식 검증
        List<Long> checkMethodIdList = actions.stream()
                .map(NewActionRequestDto::getCheckMethodId)
                .distinct().toList();

        if(!habitQuery.isUserCheckMethod(userId, checkMethodIdList)) return false;

        // 카테고리 검증
        if (!habitQuery.getHabitCategoryById(request.getCategoryId())) return false;

        // frequency 검증
        int frequency = request.getFrequency();
        int freqUnit = request.getFreqUnit();

        switch (freqUnit) {
            case 1:
                // TODO Habit FreqUnit에 따른 Frequency 기준
                break;
            case 2:
                if (frequency > 7 || frequency < 1) return false;
            case 3:
                // TODO Habit FreqUnit에 따른 Frequency 기준
                if (frequency > 28 || frequency < 1) return false;
        }

        return true;
    }

    @Override
    public HabitCreationInfo createHabit(CreateHabitRequestDto reqDto) {
        NewHabitRequestDto request = convertRequest(reqDto);
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();

        Habit habit = HabitMapper.toEntity(request);
        Long habitId = habitQuery.saveHabit(habit).getId();

        // Action DB 저장
        List<Action> actions = ActionMapper.toEntities(habitId, request);
        habitQuery.saveActionList(actions);

        return new HabitCreationInfo(habitId, actions, start, end);
    }

    @Override
    public Long createUserHabit(Long userId, CreateHabitRequestDto reqDto, HabitCreationInfo habitInfo) {
        NewHabitRequestDto request = convertRequest(reqDto);

        // 오래된 순 actionDto 정렬
        List<NewActionRequestDto> requestActions = request.getActions().stream()
                .sorted(Comparator.comparing(NewActionRequestDto::getActionDate))
                .toList();

        // action 오름차 정렬 (오래된 순)
        List<Action> actions = habitInfo.getActions().stream()
                .sorted(Comparator.comparing(action -> action.getSequence() * 100 + action.getFreqSeq()))
                .toList();

        // UserHabit을 DB에 저장하고 생성된 ID 반환
        UserHabit userHabit = UserHabitMapper.toEntity(habitInfo, userId, request.getIsPublic());
        Long userHabitId = habitQuery.saveUserHabit(userHabit);

        // UserAction 생성 및 저장
        List<UserAction> userActions = new ArrayList<>();
        for (int i = 0; i < requestActions.size(); i++) {
            Long actionId = actions.get(i).getId();
            LocalDate actionDate = requestActions.get(i).getActionDate();

            UserAction userAction = UserActionMapper.toEntity(userHabitId, actionId, actionDate);
            userActions.add(userAction);
        }
        habitQuery.saveUserActionList(userActions);

        return userHabitId;
    }

    private NewHabitRequestDto convertRequest(CreateHabitRequestDto reqDto) {
        if (!(reqDto instanceof NewHabitRequestDto)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return (NewHabitRequestDto) reqDto;
    }
}
