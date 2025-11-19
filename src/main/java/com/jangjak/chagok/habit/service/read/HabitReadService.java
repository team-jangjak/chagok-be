package com.jangjak.chagok.habit.service.read;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.response.ActionResDto;
import com.jangjak.chagok.habit.dto.response.CalendarViewResDto;
import com.jangjak.chagok.habit.dto.response.HabitDashboardResDto;
import com.jangjak.chagok.habit.dto.response.HabitDetailResDto;
import com.jangjak.chagok.habit.dto.value.ActionAndUserActionView;
import com.jangjak.chagok.habit.dto.value.CalendarInfo;
import com.jangjak.chagok.habit.dto.value.PopularCategoryDto;
import com.jangjak.chagok.habit.dto.value.ProgressRateInfo;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.entity.HabitCategory;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitState;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * 사용자 습관 대시보드 조회
     */
    public List<HabitDashboardResDto> getHabitDashboard(Long id) {
        //habit, 시간 가장 가까운 action, user_action 정보, user_habit 가져오기
        List<UserHabit> userHabitList = getUserHabits(id);

        List<Long> userHabitIds = userHabitList.stream()
                .map(UserHabit::getId)
                .toList();

        List<Long> habitIds = userHabitList.stream()
                .map(UserHabit::getHabitId)
                .toList();

        log.info("UserHabitIds: {}, habitIds: {}", userHabitIds, habitIds);

        // 사용자가 진행하고 있는 습관들
        List<Habit> habitList = habitQuery.findAllById(habitIds);

        // 카테고리 이름 조회
        Set<Long> categoryIds = habitList.stream()
                .map(Habit::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<HabitCategory> categories = habitQuery.findByIdIn(categoryIds);
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(HabitCategory::getId, HabitCategory::getName));

        // 방법 1. userHabitId -> habitId 맵
        Map<Long, Long> userHabitToHabitId = userHabitList.stream()
                .collect(Collectors.toMap(UserHabit::getId, UserHabit::getHabitId));


        Map<Long, Habit> habitMap = habitList.stream()
                .collect(Collectors.toMap(Habit::getId, Function.identity()));

        // 진행률 계산
        Map<Long, Integer> progressRateMap = habitQuery.findProgressRates(userHabitIds)
                .stream()
                .collect(Collectors.toMap(
                        ProgressRateInfo::getUserHabitId,
                        r -> r.getProgressRate() == null ? 0 : r.getProgressRate()
                ));


//        log.info("habitMap: {}", habitMap);

        return habitQuery.findNextUpcomingPerUserHabit(userHabitIds).stream()
                .map(r -> {
                    Long habitId = userHabitToHabitId.get(r.getId()); // r.getId() = userHabitId
                    Habit h = habitMap.get(habitId);
                    Long categoryId = (h != null) ? h.getCategoryId() : null;
                    String categoryName = (categoryId != null) ? categoryNameMap.get(categoryId) : null;

                    return HabitDashboardResDto.builder()
                            .frequencyUnit(r.getFrequencyUnit())
                            .id(r.getId())
                            .image(r.getImage())
//                            .categoryName(categoryNameMap.getOrDefault(r.getCategoryId(), "기타"))
                            .categoryName(categoryName)

                            .actionContent(r.getActionContent())
                            .actionSequence(r.getActionSequence())
                            .actionFreqSeq(r.getActionFreqSeq())

                            .actionDate(r.getActionDate())
                            .delayCount(r.getDelayCount())

                            //진행률
                            .progressRate(progressRateMap.getOrDefault(r.getId(), 0))
                            .build();
                })
                .toList();

    }

    private List<UserHabit> getUserHabits(Long id) {
        List<UserHabit> userHabitList = habitQuery.findByUserIdAndState(id, HabitState.IN_PROGRESS);

        if (userHabitList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        return userHabitList;
    }

    /**
     * 캘린더 조회
     */
    public List<CalendarViewResDto> getCalendarView(Long id, Integer year, Integer month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        List<UserHabit> userHabitList = getUserHabits(id);
        List<Long> userHabitIds = userHabitList.stream()
                .map(UserHabit::getId)
                .toList();

        List<CalendarInfo> calendarInfo = habitQuery.findCalendarInfo(start, end, userHabitIds);

        // 날짜별 그룹핑
        Map<LocalDate, List<ActionResDto>> byDate = calendarInfo.stream()
                .collect(Collectors.groupingBy(
                        CalendarInfo::getActionDate,
                        LinkedHashMap::new,
                        Collectors.mapping(r -> ActionResDto.builder()
                                        .id(r.getUserHabitId())
                                        .userActionId(r.getUserActionId())
                                        .actionContent(r.getActionContent())
                                        .isCompleted(r.getIsCompleted())
                                        .build(),
                                Collectors.toList())
                ));

        // CalendarViewResDto 리스트로 변환 (날짜 오름차순)
        return byDate.entrySet().stream()
                .map(e -> CalendarViewResDto.builder()
                        .actionDate(e.getKey())
                        .actionResDto(e.getValue())
                        .build())
                .sorted(Comparator.comparing(CalendarViewResDto::getActionDate))
                .toList();
    }

    public HabitDetailResDto getHabitDetail(Long userActionId) {
        ActionAndUserActionView habitActionDetail = habitQuery.findHabitActionDetail(userActionId);

        if (habitActionDetail == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return HabitDetailResDto.builder()
                // habit
                .habitTitle(habitActionDetail.getHabitTitle())
                .frequency(habitActionDetail.getFrequency())
                .frequencyUnit(habitActionDetail.getFrequencyUnit())
                .id(habitActionDetail.getId())  // userHabitId
                .categoryName(habitActionDetail.getCategoryName())
                .image(habitActionDetail.getImage())

                // action
                .actionContent(habitActionDetail.getActionContent())
                .checkMethodId(habitActionDetail.getCheckMethodId())
                .actionSequence(habitActionDetail.getActionSequence())
                .actionFreqSeq(habitActionDetail.getActionFreqSeq())

                // user_action
                .actionDate(habitActionDetail.getActionDate())
                .delayCount(habitActionDetail.getDelayCount())
                .isCompleted("Y".equalsIgnoreCase(habitActionDetail.getIsCompleted()) ? YN.Y : YN.N)

                // 진행률
                .progressRate(Optional.ofNullable(habitActionDetail.getProgressRate()).orElse(0))
                .build();
    }

}
