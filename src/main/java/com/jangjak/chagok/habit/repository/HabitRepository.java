package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByHabitIdAndIsTemplate(Long id, YN isTemplate);
}
