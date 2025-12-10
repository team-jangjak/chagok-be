package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateActionRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateHabitRequestDto;
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
    public HabitCreationInfo createHabit(CreateHabitRequestDto reqDto) {
        TemplateHabitRequestDto request = convertRequest(reqDto);

        Long habitId = request.getHabitId();
        List<Action> actionList = habitQuery.getActionsByHabitId(habitId);

        // Action 개수 검증
        if (request.getActions().size() != actionList.size()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return new HabitCreationInfo(habitId, actionList, request.getStartDate(), request.getEndDate());
    }

    @Override
    public Long createUserHabit(Long userId, CreateHabitRequestDto reqDto, HabitCreationInfo habitInfo) {
        TemplateHabitRequestDto request = convertRequest(reqDto);

        // 오래된 순 actionDto 정렬
        List<TemplateActionRequestDto> requestActions = request.getActions().stream()
                .sorted(Comparator.comparing(TemplateActionRequestDto::getActionDate))
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
            Long actionId = actions.get(i).getActionId();
            LocalDate actionDate = requestActions.get(i).getActionDate();

            // actionId가 실제로 존재하는지
            if (!actionId.equals(requestActions.get(i).getActionId())) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            UserAction userAction = UserActionMapper.toEntity(userHabitId, actionId, actionDate);
            userActions.add(userAction);
        }
        habitQuery.saveUserActionList(userActions);

        return userHabitId;
    }

    private TemplateHabitRequestDto convertRequest(CreateHabitRequestDto reqDto) {
        if (!(reqDto instanceof TemplateHabitRequestDto)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return (TemplateHabitRequestDto) reqDto;
    }
}
