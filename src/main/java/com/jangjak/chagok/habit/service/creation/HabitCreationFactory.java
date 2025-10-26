package com.jangjak.chagok.habit.service.creation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HabitCreationFactory {

    private final NewHabitCreation newHabitCreation;
    private final ModifyHabitCreation modifyHabitCreation;
    private final TemplateHabitCreation templateHabitCreation;

    public HabitCreation getHabitCreation(HabitCreationType type) {
        return switch (type) {
            case NEW -> newHabitCreation;
            case MODIFY -> modifyHabitCreation;
            case TEMPLATE -> templateHabitCreation;
        };
    }
}
