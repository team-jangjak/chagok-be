package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.HabitCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HabitCategoryRepository extends JpaRepository<HabitCategory, Long> {

    @Query("""
        select c.name
        from Habit h, HabitCategory c
        where h.id = :habitId
          and c.id = h.categoryId
    """)
    Optional<String> findCategoryNameByHabitId(@Param("habitId") Long habitId);
}