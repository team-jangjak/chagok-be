package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.entity.Habit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HabitMapper {
    public static Habit toEntity(NewHabitRequestDto reqDto) {
       return Habit.builder()
               .categoryId(reqDto.getCategoryId())
               .title(reqDto.getTitle())
               .frequency(reqDto.getFrequency())
               .freqUnit(reqDto.getFreqUnit())
               .isPublic(reqDto.isTemplate() ? YN.Y : YN.N)
               .build();
    }
}
