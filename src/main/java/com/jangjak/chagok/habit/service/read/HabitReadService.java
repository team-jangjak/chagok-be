package com.jangjak.chagok.habit.service.read;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.response.*;
import com.jangjak.chagok.habit.dto.value.ActionAndUserActionView;
import com.jangjak.chagok.habit.dto.value.CalendarInfo;
import com.jangjak.chagok.habit.dto.value.PopularCategoryDto;
import com.jangjak.chagok.habit.dto.value.ProgressRateInfo;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.enums.HabitState;
import com.jangjak.chagok.habit.repository.CheckMethodDetailRepository;
import com.jangjak.chagok.habit.repository.CheckMethodRepository;
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

    private final CheckMethodRepository checkMethodRepository;
    private final CheckMethodDetailRepository checkMethodDetailRepository;


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
        List<ProgressRateInfo> rawList = habitQuery.findProgressRates(userHabitIds);
        Map<Long, Integer> progressRateMap = getProgressRateMap(rawList);


//        log.info("habitMap: {}", habitMap);

        return habitQuery.findNextUpcomingPerUserHabit(userHabitIds).stream()
                .map(r -> {
                    Long habitId = userHabitToHabitId.get(r.getUserHabitId()); //userHabitId
                    Habit h = habitMap.get(habitId);
                    Long categoryId = (h != null) ? h.getCategoryId() : null;
                    String categoryName = (categoryId != null) ? categoryNameMap.get(categoryId) : null;

                    return HabitDashboardResDto.builder()
                            .frequencyUnit(r.getFrequencyUnit())
                            .id(r.getUserActionId()) //userActionId
                            .image(r.getImage())
//                            .categoryName(categoryNameMap.getOrDefault(r.getCategoryId(), "기타"))
                            .categoryName(categoryName)

                            .actionContent(r.getActionContent())
                            .actionSequence(r.getActionSequence())
                            .actionFreqSeq(r.getActionFreqSeq())

                            .actionDate(r.getActionDate())
                            .delayCount(r.getDelayCount())

                            //진행률
                            .progressRate(progressRateMap.getOrDefault(r.getUserHabitId(), 0))
                            .build();
                })
                .toList();

    }

    private Map<Long, Integer> getProgressRateMap(List<ProgressRateInfo> rawList) {
        return rawList.stream()
                .collect(Collectors.toMap(
                        ProgressRateInfo::getUserHabitId,
                        raw -> calculateProgressRate(raw.getTotalCount(), raw.getCompletedCount())
                ));
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
                                        .id(r.getUserActionId()) //userActionId
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
    public HabitDetailResDto getHabitDetail(Long userActionId) {
        // userId와 비교 검증 추가

        ActionAndUserActionView habitActionDetail = habitQuery.findHabitActionDetail(userActionId);

        if (habitActionDetail == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        // 진행률 조회
        UserAction userActionById = habitQuery.getUserActionById(userActionId);
        int total = habitQuery.countByUserHabitId(userActionById.getUserHabitId());
        int completed = habitQuery.countByUserHabitIdAndIsCompleted(userActionById.getUserHabitId(), "Y");
        int progressRate = calculateProgressRate(total, completed);

        // 인증 방식 정보 가져오기 userAction에 checkMethodId가 없어서
        // userAction - Action - CheckMethod 경로로 접근

        return HabitDetailResDto.builder()
                // habit
                .habitTitle(habitActionDetail.getHabitTitle())
                .frequency(habitActionDetail.getFrequency())
                .frequencyUnit(habitActionDetail.getFrequencyUnit())
                .categoryName(habitActionDetail.getCategoryName())
                .image(habitActionDetail.getImage())

                // action
                .actionContent(habitActionDetail.getActionContent())
                .checkMethodId(habitActionDetail.getCheckMethodId())
                .actionSequence(habitActionDetail.getActionSequence())
                .actionFreqSeq(habitActionDetail.getActionFreqSeq())

                // user_action
                .id(habitActionDetail.getUserActionId())  // userActionId
                .actionDate(habitActionDetail.getActionDate())
                .delayCount(habitActionDetail.getDelayCount())
                .isCompleted("Y".equalsIgnoreCase(habitActionDetail.getIsCompleted()) ? YN.Y : YN.N)

                // 진행률
                .progressRate(progressRate)
                .build();
    }

    /**
     * 진행률 계산 로직
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

    /**
     * Action 인증을 위한 인증 방식(check method) 조회
     *
     * @return
     */
    public CheckMethodResDto checkMethodOfAction(Long id, Long actionId) {
        Action actionById = habitQuery.findActionById(actionId);

        CheckMethod checkMethod = checkMethodRepository.findById(actionById.getCheckMethodId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));

        List<CheckMethodDetail> details = checkMethodDetailRepository.findByCheckMethodIdOrderByMethodOrderAsc(actionById.getCheckMethodId());

        if (details.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        List<CheckMethodDetailRestDto> detailResDto = details.stream()
                .map(detail -> CheckMethodDetailRestDto.builder()
                        .type(detail.getType())
                        .value(detail.getValue())
                        .build()
                ).toList();

        return CheckMethodResDto.builder()
                .id(actionById.getCheckMethodId())
                .title(checkMethod.getTitle())
                .details(detailResDto)
                .build();
    }
}
