package com.jangjak.chagok.habit.service.create;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create2.ActionCreateRequestDto;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserAction;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitCategory;
import com.jangjak.chagok.habit.mapper.UserActionMapper;
import com.jangjak.chagok.habit.mapper.UserHabitMapper;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.habit.service.create.factory.HabitCreation;
import com.jangjak.chagok.habit.service.create.factory.HabitCreationFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 습관 관련 비즈니스 로직을 처리하는 서비스 클래스
 * <p>
 * 주요 기능:
 * - 사용자 정의 습관 생성 및 기존 습관 재활용
 * - 습관과 연관된 액션들의 사용자별 상태 관리
 * - Factory 패턴을 통한 습관 생성 전략 분리
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HabitCreateService {
    private final HabitCreationFactory creationFactory;
    private final HabitQuery habitQuery;

    /**
     * 습관 생성 (new, modify, template)
     *
     * @param userId Token에서 가져온 userId
     * @param reqDto 습관 생성 공통 요청 데이터
     * @return userHabitId
     */
    @Transactional
    public Long createHabit(Long userId, HabitCreateRequestDto reqDto) {
        // 유효 시작 시간 설정
        LocalDateTime validStartDt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 유효성 검증
        validateRequest(reqDto);

        // 습관/행위 생성
        HabitCreation manager = creationFactory.getHabitCreation(reqDto.getRequestType());
        HabitCreationInfo habitInfo = manager.createHabit(reqDto, validStartDt);

        // 사용자 습관/행위 생성
        return createUserHabit(reqDto, habitInfo, userId);
    }


    // 요청값 검증
    private void validateRequest(HabitCreateRequestDto reqDto) {
        // 카테고리가 실제로 존재하는 지 검증
        HabitCategory habitCategory = HabitCategory.fromValue(reqDto.getHabitCategory().intValue());
        if (habitCategory == HabitCategory.NONE) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    // 사용자 습관 생성
    private Long createUserHabit(HabitCreateRequestDto reqDto, HabitCreationInfo habitInfo, Long userId) {
        // 오래된 순 actionDto 정렬
        List<ActionCreateRequestDto> requestActions = reqDto.getActions().stream()
                .sorted(Comparator.comparing(ActionCreateRequestDto::getActionDate))
                .toList();

        // action 오름차 정렬 (오래된 순)
        List<Action> actions = habitInfo.getActions().stream()
                .sorted(Comparator.comparing(action -> action.getSequence() * 1000 + action.getFreqSeq()))
                .toList();

        // UserHabit을 DB에 저장하고 생성된 ID 반환
        UserHabit userHabit = UserHabitMapper.toEntity(habitInfo, userId, reqDto);
        Long userHabitId = habitQuery.saveUserHabit(userHabit);

        // UserAction 생성 및 저장
        List<UserAction> userActions = new ArrayList<>();
        for (int i = 0; i < requestActions.size(); i++) {
            Long actionId = actions.get(i).getId().getActionId();
            LocalDateTime actionDate = requestActions.get(i).getActionDate();

            UserAction userAction = UserActionMapper.toEntity(userId, habitInfo.getHabitId(), userHabitId, actionId, LocalDate.from(actionDate));
            userActions.add(userAction);
        }
        habitQuery.saveUserActionList(userActions);

        return userHabitId;
    }
}
