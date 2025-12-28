package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findByIdHabitIdAndIsTemplate(Long id, YN isTemplate);

    Optional<Habit> findByIdHabitId(Long habitId);

    @Modifying
    @Query(value = """
        UPDATE habit
        SET valid_end_at = :validStDt
        WHERE habit_id = :habitId
        AND valid_end_at = :max
    """, nativeQuery = true)
    void expireHabit(Long habitId, LocalDateTime validStDt, LocalDateTime max);
}
