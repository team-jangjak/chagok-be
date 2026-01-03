package com.jangjak.chagok.habit.service.read;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.response.*;
import com.jangjak.chagok.habit.dto.value.ActionAndUserActionView;
import com.jangjak.chagok.habit.dto.value.CalendarInfo;
import com.jangjak.chagok.habit.dto.value.ProgressRateInfo;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.enums.HabitState;
import com.jangjak.chagok.habit.repository.CheckMethodRepository;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.habit.enums.HabitCategory;
import com.jangjak.chagok.habit.repository.QueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final CheckMethodRepository checkMethodRepository;
    private final QueryRepository queryRepository;


    /**
     * 인기 습관 카테고리 정보 조회
     */
//    public List<PopularCategoryDto> getPopularHabitCategory() {
//        return habitQuery.getPopularHabitCategory();
//    }

    /**
     * 사용자 습관 대시보드 조회
     */
    public List<HabitDashboardResDto> getHabitDashboard(Long id) {
        //habit, 시간 가장 가까운 action, user_action 정보, user_habit 가져오기
        // 진행 중인 userHabit 조회
        List<UserHabit> userHabitList = getUserHabits(id, HabitState.IN_PROGRESS);

        if (userHabitList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userHabitIds = userHabitList.stream().map(UserHabit::getUserHabitId).toList();
        List<Long> habitIds = userHabitList.stream().map(UserHabit::getHabitId).distinct().toList();

        // 관련된 모든 버전의 Habit을 한꺼번에 조회
        List<Habit> allHabitVersions = habitQuery.findAllByHabitId(habitIds);
        Map<Long, List<Habit>> habitVersionsByHabitId = allHabitVersions.stream()
                .sorted(Comparator.comparing(Habit::getValidStartAt).reversed())
                .collect(Collectors.groupingBy(h -> h.getId().getHabitId()));

        // 가장 가까운 action 조회
        List<ActionAndUserActionView> upcomingActions = habitQuery.findNextUpcomingPerUserHabit(userHabitIds);

        if (upcomingActions.isEmpty()) {
            return Collections.emptyList();
        }

        // 진행률 계산
        Map<Long, Integer> progressRateMap = getProgressRateMap(habitQuery.findProgressRates(userHabitIds, YN.Y));


        Map<Long, UserHabit> userHabitMap = userHabitList.stream()
                .collect(Collectors.toMap(UserHabit::getUserHabitId, Function.identity()));

//        log.info("userHabitMap: {}", userHabitMap);


        return upcomingActions.stream()
                .map(r -> {
                    UserHabit userHabit = userHabitMap.get(r.getUserHabitId());
                    if (userHabit == null) return null;

                    LocalDateTime createdAt = userHabit.getCreatedAt();
                    List<Habit> versions = habitVersionsByHabitId.getOrDefault(userHabit.getHabitId(), List.of());

                    Habit matchedHabit = versions.stream()
                            .filter(h -> !createdAt.isBefore(h.getValidStartAt())
                                         && createdAt.isBefore(h.getId().getValidEndAt()))
                            .findFirst()
                            .orElse(null);

                    HabitCategory categoryName = HabitCategory.OTHER;
                    if (matchedHabit != null) {
                        categoryName = HabitCategory.fromValue(matchedHabit.getCategory().intValue());
                        if (categoryName == HabitCategory.NONE) categoryName = HabitCategory.OTHER;
                    }

                    return HabitDashboardResDto.builder()
                            .id(r.getUserActionId())
                            .actionDate(r.getActionDate())
                            .actionContent(r.getActionContent())
                            .actionSequence(r.getActionSequence())
                            .actionFreqSeq(r.getActionFreqSeq())
                            .delayCount(r.getDelayCount())
                            .progressRate(progressRateMap.getOrDefault(r.getUserHabitId(), 0))
                            // Habit에서 가져와야 하는 정보
                            .frequencyUnit(matchedHabit != null ? matchedHabit.getFreqUnit() : null)
                            .image(matchedHabit != null ? matchedHabit.getImage() : null)
                            .categoryName(categoryName)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

    }

    private Map<Long, Integer> getProgressRateMap(List<ProgressRateInfo> rawList) {
        return rawList.stream()
                .collect(Collectors.toMap(
                        ProgressRateInfo::getUserHabitId,
                        raw -> calculateProgressRate(raw.getTotalCount().intValue(), raw.getCompletedCount().intValue())
                ));
    }

    private List<UserHabit> getUserHabits(Long id, HabitState... states) {
        List<UserHabit> userHabitList = queryRepository.findByUserIdAndStates(id, Arrays.asList(states));

        if (userHabitList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }
        return userHabitList;
    }

    /**
     * 캘린더 조회
     */
    public List<CalendarViewResDto> getCalendarView(Long id, Integer year, Integer month, Long userHabitId, HabitState state) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        List<CalendarInfo> calendarInfo = queryRepository.findCalendarInfo(id, start, end, userHabitId, state);

        // 날짜별 그룹핑
        Map<LocalDate, List<ActionResDto>> byDate = calendarInfo.stream()
                .collect(Collectors.groupingBy(
                        CalendarInfo::getActionDate,
                        LinkedHashMap::new,
                        Collectors.mapping(r -> ActionResDto.builder()
                                        .id(r.getUserActionId()) //userActionId
                                        .habit(r.getUserHabitId()) // 습관별로 시각적으로 구분할 수 있도록, userHabitId(or habitId)도 추가
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

    /**
     * 습관&액션 상세 조회
     */
    public HabitDetailResDto getHabitDetail(Long id, Long userActionId) {
        UserAction userAction = validate(id, userActionId);

        Action action = habitQuery.findByActionIdAndCreatedAt(userAction.getActionId(), userAction.getCreatedAt());
        Habit habit = habitQuery.findByHabitIdAndCreatedAt(action.getHabitId(), userAction.getCreatedAt());
        CheckMethod checkMethod = queryRepository.findByCheckMethodIdAndCreatedAt(action.getCheckMethodId(), action.getCreatedAt()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));


        // 진행률 조회
        int total = habitQuery.countByUserHabitId(userAction.getUserHabitId());
        int completed = habitQuery.countByUserHabitIdAndIsCompleted(userAction.getUserHabitId(), YN.Y);
        int progressRate = calculateProgressRate(total, completed);
        log.info("total:{} , completed:{} , progressRate:{}", total, completed, progressRate);


        return HabitDetailResDto.builder()
                // habit
                .habitTitle(habit.getTitle())
                .frequency(habit.getFrequency())
                .frequencyUnit(habit.getFreqUnit())
                .categoryName(HabitCategory.fromValue(habit.getCategory().intValue()) == HabitCategory.NONE
                        ? HabitCategory.OTHER
                        : HabitCategory.fromValue(habit.getCategory().intValue())
                )

                .image(habit.getImage())

                // action
                .actionContent(action.getContent())
                .checkMethodTitle(checkMethod.getTitle())
                .actionSequence(action.getSequence())
                .actionFreqSeq(action.getFreqSeq())

                // user_action
                .id(userAction.getUserActionId())  // userActionId
                .actionDate(userAction.getActionDate())
                .delayCount(userAction.getDelayCount())
                .isCompleted(userAction.getIsCompleted())

                // 진행률
                .progressRate(progressRate)
                .build();
    }

    public UserAction validate(Long id, Long userActionId) {
        UserAction userActionById = habitQuery.getUserActionById(userActionId);

        // userId와 비교 검증
        UserHabit userHabit = habitQuery.getUserHabit(userActionById.getUserHabitId());
        if (!userHabit.getUserId().equals(id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return userActionById;
    }

    /**
     * 진행률 계산 로직
     *
     * @param total
     * @param completed
     * @return
     */
    private int calculateProgressRate(int total, int completed) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.floor((completed * 100.0) / total);
    }
}
