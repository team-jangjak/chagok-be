package com.jangjak.chagok.habit.service.read;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.response.ActionAndUserActionDto;
import com.jangjak.chagok.habit.dto.value.PopularCategoryDto;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitState;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitReadService {

    /**
     * 습관 관련 데이터베이스 조회/저장을 담당하는 쿼리 클래스
     */
    private final HabitQuery habitQuery;


    /**
     * 인기 습관 카테고리 정보 조회
     */
    public List<PopularCategoryDto> getPopularHabitCategory() {
        return habitQuery.getPopularHabitCategory();
    }

    public void getHabitDashboard(Long id) {
        //habit, 시간 가장 가까운 action, user_action 정보, user_habit 가져오기
        List<UserHabit> userHabitList = habitQuery.findByUserIdAndState(id, HabitState.IN_PROGRESS);

        if (userHabitList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        List<Long> UserHabitIds = userHabitList.stream()
                .map(UserHabit::getId)
                .toList();

        List<Long> habitIds = userHabitList.stream()
                .map(UserHabit::getHabitId)
                .toList();

        log.info("UserHabitIds: {}, habitIds: {}", UserHabitIds, habitIds);

        // 사용자가 진행하고 있는 습관들
        List<Habit> habitList = habitQuery.findAllById(habitIds);

        List<ActionAndUserActionDto> nextUpcomingPerUserHabit = habitQuery.findNextUpcomingPerUserHabit(UserHabitIds, habitIds)
                .stream()
                .map(v -> new ActionAndUserActionDto(
                        v.getHabitId(),
                        v.getActionId(),
                        v.getActionContent(),
                        v.getCheckMethodId(),
                        v.getActionSequence(),
                        v.getActionFreqSeq(),
                        v.getUserActionId(),
                        v.getUserHabitId(),
                        v.getActionDate(),
                        v.getDelayCount(),
                        YN.from(v.getIsCompleted())
                ))
                .toList();

        log.info("액션 대시보드 조회: {}", nextUpcomingPerUserHabit);
    }
}
