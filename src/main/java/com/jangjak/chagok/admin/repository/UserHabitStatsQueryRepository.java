package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.dto.info.HabitProgressDto;

import java.time.LocalDate;
import java.util.List;

public interface UserHabitStatsQueryRepository {
    List<HabitProgressDto> findHabitStatsByUserIds(List<Long> userIds, LocalDate baseDate);

    Double findGlobalAverageProgress();

}
