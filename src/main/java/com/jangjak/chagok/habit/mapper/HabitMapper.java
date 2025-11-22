package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.enums.HabitCategory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HabitMapper {
    public static Habit toEntity(NewHabitRequestDto reqDto) {
       return Habit.builder()
               .categoryId(HabitCategory.toEnum(reqDto.getCategoryId().intValue()))
               .title(reqDto.getTitle())
               .frequency(reqDto.getFrequency())
               .freqUnit(reqDto.getFreqUnit())
               .image("") // TODO 이미지 처리
               .isTemplate(reqDto.getIsTemplate() ? YN.Y : YN.N)
               .build();
    }

    public static Habit toEntity(ModifyHabitRequestDto reqDto, Habit oldHabit) {
        return Habit.builder()
                .categoryId(reqDto.getCategoryId() != null ? HabitCategory.toEnum(reqDto.getCategoryId().intValue()) : oldHabit.getCategoryId())
                .title(reqDto.getTitle() != null ? reqDto.getTitle() : oldHabit.getTitle())
                .frequency(reqDto.getFrequency() != null ? reqDto.getFrequency() : oldHabit.getFrequency())
                .freqUnit(reqDto.getFreqUnit() != null ? reqDto.getFreqUnit() : oldHabit.getFreqUnit())
                .image("") // TODO 이미지 처리
                .isTemplate(YN.N)
                .build();
    }
}
