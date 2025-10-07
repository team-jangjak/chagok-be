package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HabitCreationFactory {

    private final HabitCreationCustom habitCreationCustom;
    private final HabitCreationExisting habitCreationExisting;

    public HabitCreationInfo createHabit(CustomHabitRequestDto reqDto) {
        HabitCreationBase habitCreation;

        if (reqDto.getHabitId() == null) { // habitId가 null이라면 새로운 생성
            habitCreation = habitCreationCustom;
        } else { // habitId가 null이 아니라면 기존 습관 재활용
            habitCreation = habitCreationExisting;
        }

        return habitCreation.createHabit(reqDto);
    }

}
