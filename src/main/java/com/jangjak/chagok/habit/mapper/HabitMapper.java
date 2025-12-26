package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.entity.Habit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HabitMapper {
    public static Habit toEntity(NewHabitRequestDto reqDto) {
       return Habit.builder()
               .category(reqDto.getCategory())
               .title(reqDto.getTitle())
               .frequency(reqDto.getFrequency())
               .freqUnit(reqDto.getFreqUnit())
               .freqCount(reqDto.getFreqCount())
               .image("") // TODO 이미지 처리
               .isTemplate(reqDto.getIsTemplate() ? YN.Y : YN.N)
               .build();
    }

    public static Habit toEntity(HabitCreateRequestDto reqDto, LocalDateTime validStDt) {
        return Habit.builder()
                .category(reqDto.getHabitCategory())
                .title(reqDto.getHabitTitle())
                .frequency(reqDto.getFrequency())
                .freqUnit(reqDto.getFreqUnit())
                .freqCount(reqDto.getFreqCount())
                .image("") // TODO 이미지 처리
                .isTemplate(reqDto.getIsTemplate() ? YN.Y : YN.N)
                .validStartAt(validStDt)
                .validEndAt(LocalDateTime.MAX)
                .build();
    }

    public static Habit toEntity(ModifyHabitRequestDto reqDto, Habit oldHabit) {
        return Habit.builder()
                .category(reqDto.getCategory() != null ? reqDto.getCategory() : oldHabit.getCategory())
                .title(reqDto.getTitle() != null ? reqDto.getTitle() : oldHabit.getTitle())
                .frequency(reqDto.getFrequency() != null ? reqDto.getFrequency() : oldHabit.getFrequency())
                .freqUnit(reqDto.getFreqUnit() != null ? reqDto.getFreqUnit() : oldHabit.getFreqUnit())
                .freqCount(reqDto.getFreqCount() != null ? reqDto.getFreqCount() : oldHabit.getFreqCount())
                .image("") // TODO 이미지 처리
                .isTemplate(YN.N)
                .build();
    }
}
