package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.*;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.entity.UserAction;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitCategory;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModifyHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;

    @Override
    public boolean validateRequest(Long userId, CreateHabitRequestDto reqDto) throws CustomException {
        ModifyHabitRequestDto request = convertRequest(reqDto);

        // 템플릿이 실제로 존재하는 지 확인
        Habit habit = habitQuery.getTemplateHabit(request.getHabitId());
        if (habit == null) return false;

        // 날짜 검증
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        if (!DateValidationUtil.validateAndParseHabitDates(start, end)) return false;

        // action 검증
        List<ModifyActionRequestDto> actions = request.getActions();
        if (actions == null || actions.isEmpty()) return false;

        // 카테고리 검증
        HabitCategory habitCategory = HabitCategory.fromValue(request.getCategory().intValue());
        if (habitCategory == HabitCategory.NONE) return false;
//        if (request.getCategoryId() != null && !habitQuery.getHabitCategoryById(request.getCategoryId())) return false;

        // frequency 검증
        Integer frequency = request.getFrequency();
        Integer freqUnit = request.getFreqUnit();

        // freqUnit을 바꾸는 경우..를 어떡하지..
        if (frequency == null && freqUnit == null) { // 기존 템플릿 빈도 유지
            return true;
        } else { // 변경점이 존재하는 경우
            // null 값 채우기
            if (frequency == null) frequency = habit.getFrequency();
            if (freqUnit == null) freqUnit = habit.getFreqUnit();

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

            // request에 값 채우기
            request.setFrequency(frequency);
            request.setFreqUnit(freqUnit);
        }

        // TODO 액션 까서 freqCount 넘는지 확인해야 함

        return true;
    }

    @Override
    public HabitCreationInfo createHabit(CreateHabitRequestDto reqDto) {
        ModifyHabitRequestDto request = convertRequest(reqDto);
        Long oldHabitId = request.getHabitId();
        Habit oldHabit = habitQuery.getHabitById(oldHabitId);

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        // 오래된 순 actionDto 정렬
        List<ModifyActionRequestDto> reqActions = request.getActions().stream()
                .sorted(Comparator.comparing(ModifyActionRequestDto::getActionDate))
                .toList();

        Map<Long, Action> actionMap = habitQuery.getActionsByHabitId(oldHabitId).stream()
                .collect(Collectors.toMap(
                        Action::getActionId,
                        Function.identity()
                ));

        // 습관 저장
        Habit habit = HabitMapper.toEntity(request, oldHabit);
        Long habitId = habitQuery.saveHabit(habit).getHabitId();

        List<Action> actionResult = new ArrayList<>();
        for (ModifyActionRequestDto reqAction : reqActions) {
            Action action = actionMap.get(reqAction.getActionId());

            Action result;
            if (reqAction.getIsModified()) { // 변경 되었다면
                if (reqAction.getActionId() == null) { // 템플릿에 없는 action이라면
                    result = ActionMapper.toEntity(habitId, reqAction);
                } else { // 템플릿에 있는 action이라면
                    if (action == null) throw new CustomException(ErrorCode.BAD_REQUEST);
                    result = ActionMapper.toEntity(habitId, reqAction, action);
                }
            } else {
                if (action == null) throw new CustomException(ErrorCode.BAD_REQUEST);
                result = ActionMapper.toEntity(habitId, action);
            }

            actionResult.add(result);
        }
        habitQuery.saveActionList(actionResult);

        return new HabitCreationInfo(habitId, actionResult, startDate, endDate);
    }

    @Override
    public Long createUserHabit(Long userId, CreateHabitRequestDto reqDto, HabitCreationInfo habitInfo) {
        ModifyHabitRequestDto request = convertRequest(reqDto);

        // 오래된 순 actionDto 정렬
        List<ModifyActionRequestDto> requestActions = request.getActions().stream()
                .sorted(Comparator.comparing(ModifyActionRequestDto::getActionDate))
                .toList();

        // action 오름차 정렬 (오래된 순)
        List<Action> actions = habitInfo.getActions().stream()
                .sorted(Comparator.comparing(action -> action.getSequence() * 100 + action.getFreqSeq()))
                .toList();

        // UserHabit을 DB에 저장하고 생성된 ID 반환
        UserHabit userHabit = UserHabitMapper.toEntity(habitInfo, userId, request.isPublic());
        Long userHabitId = habitQuery.saveUserHabit(userHabit);

        // UserAction 생성 및 저장
        List<UserAction> userActions = new ArrayList<>();
        for (int i = 0; i < requestActions.size(); i++) {
            Long actionId = actions.get(i).getActionId();
            LocalDate actionDate = requestActions.get(i).getActionDate();

            UserAction userAction = UserActionMapper.toEntity(userHabitId, actionId, actionDate);
            userActions.add(userAction);
        }
        habitQuery.saveUserActionList(userActions);

        return userHabitId;
    }

    private ModifyHabitRequestDto convertRequest(CreateHabitRequestDto reqDto) {
        if (!(reqDto instanceof ModifyHabitRequestDto)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return (ModifyHabitRequestDto) reqDto;
    }
}
