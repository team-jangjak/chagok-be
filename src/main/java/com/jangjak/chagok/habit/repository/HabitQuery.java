package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.response.ActionAndUserActionView;
import com.jangjak.chagok.habit.dto.value.PopularCategoryDto;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.enums.HabitState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HabitQuery {
    private final HabitRepository habitRepository;
    private final ActionRepository actionRepository;
    private final UserHabitRepository userHabitRepository;
    private final UserActionRepository userActionRepository;
    private final HabitCategoryRepository habitCategoryRepository;
    private final PopularHabitCategoryRepository popularHabitCategoryRepository;
    private final CheckMethodRepository checkMethodRepository;

    public Habit getHabitById(Long habitId) {
        return habitRepository.findById(habitId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    public List<Action> getActionsByHabitId(Long habitId) {
        return actionRepository.getActionsByHabitId(habitId);
    }

    public Long saveUserHabit(UserHabit userHabit) {
        return userHabitRepository.save(userHabit).getId();
    }

    public Habit saveHabit(Habit habit) {
        return habitRepository.save(habit);
    }

    // --- 추후 비동기 처리를 통해 대량 데이터 주입 시 성능 최적화 여부 고려 --- (굳이긴 합니다...)
    public void saveUserActionList(List<UserAction> userActions) {
        userActionRepository.saveAll(userActions);
    }

    public void saveActionList(List<Action> actions) {
        actionRepository.saveAll(actions);
    }

    public boolean getHabitCategoryById(Long categoryId) {
        return habitCategoryRepository.findById(categoryId).isPresent();
    }

    public boolean isUserCheckMethod(Long userId, List<Long> checkMethodIdList) {
        List<CheckMethod> methodList = checkMethodRepository.getCheckMethodsByIdIn(checkMethodIdList);
        for (CheckMethod method : methodList) {
            if(!method.getUserId().equals(userId)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 인기 습관 카테고리 정보 조회
     */
    public List<PopularCategoryDto> getPopularHabitCategory() {
        return popularHabitCategoryRepository.findAllWithCategoryName();
    }

    public List<UserHabit> findByUserIdAndState(Long userId, HabitState habitState){
        return userHabitRepository.findByUserIdAndState(userId, habitState);
    }

    public List<Habit> findAllById(List<Long> habitIds){
        return habitRepository.findAllById(habitIds);
    }

    public List<ActionAndUserActionView> findNextUpcomingPerUserHabit(List<Long> userHabitIds, List<Long> habitIds){
        return userActionRepository.findNextUpcomingPerUserHabit(userHabitIds, habitIds);
    }

    public boolean isTemplateHabit(Long habitId) {
        return habitRepository.findByIdAndIsTemplate(habitId, YN.Y).isPresent();
    }

    // 일치하는 Habit이 없다면 null 반환
    public Habit getTemplateHabit(Long habitId) {
        return habitRepository.findByIdAndIsTemplate(habitId, YN.Y).orElse(null);
    }
}
