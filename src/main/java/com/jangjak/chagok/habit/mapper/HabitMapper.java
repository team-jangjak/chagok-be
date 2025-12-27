package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.request.update.HabitUpdateRequestDto;
import com.jangjak.chagok.habit.entity.Habit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HabitMapper {
    public static Habit toEntity(HabitCreateRequestDto reqDto, LocalDateTime validStDt) {
        return Habit.builder()
                .category(reqDto.getHabitCategory())
                .title(reqDto.getHabitTitle())
                .frequency(reqDto.getFrequency())
                .freqUnit(reqDto.getFreqUnit())
                .freqCount(reqDto.getFreqCount())
                .image(reqDto.getHabitImage())
                .isTemplate(reqDto.getIsTemplate() ? YN.Y : YN.N)
                .validStartAt(validStDt)
                .validEndAt(LocalDateTime.MAX)
                .build();
    }

    public static Habit toEntity(HabitCreateRequestDto reqDto, Habit templateHabit, LocalDateTime validStDt) {
        return Habit.builder()
                .category(templateHabit.getCategory())
                .title(templateHabit.getTitle())
                .frequency(reqDto.getFrequency())
                .freqUnit(reqDto.getFreqUnit())
                .freqCount(reqDto.getFreqCount())
                .image(templateHabit.getImage())
                .isTemplate(YN.N)
                .validStartAt(validStDt)
                .validEndAt(LocalDateTime.MAX)
                .build();
    }

    public static Habit updateFrom(Habit existing, HabitUpdateRequestDto reqDto, LocalDateTime validStDt) {
        return Habit.builder()
                .category(reqDto.getHabitCategory() != null ? reqDto.getHabitCategory() : existing.getCategory())
                .title(reqDto.getHabitTitle() != null ? reqDto.getHabitTitle() : existing.getTitle())
                .frequency(reqDto.getFrequency() != null ? reqDto.getFrequency() : existing.getFrequency())
                .freqUnit(reqDto.getFreqUnit()  != null ? reqDto.getFreqUnit() : existing.getFreqUnit())
                .freqCount(reqDto.getFreqCount() != null ? reqDto.getFreqCount() : existing.getFreqCount())
                .image(reqDto.getHabitImage() != null ? reqDto.getHabitImage() : existing.getImage())
                .isTemplate(reqDto.getIsTemplate() != null ? (reqDto.getIsTemplate() ? YN.Y : YN.N) : existing.getIsTemplate())
                .validStartAt(validStDt)
                .validEndAt(LocalDateTime.MAX)
                .build();
    }
}
