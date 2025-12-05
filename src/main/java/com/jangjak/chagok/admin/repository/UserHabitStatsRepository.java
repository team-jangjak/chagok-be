package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.entity.UserHabitStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHabitStatsRepository extends JpaRepository<UserHabitStats, Long> {
    List<UserHabitStats> findByUserIdIn(List<Long> userIds);
}
