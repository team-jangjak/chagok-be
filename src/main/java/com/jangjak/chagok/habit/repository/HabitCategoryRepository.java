package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.HabitCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitCategoryRepository extends JpaRepository<HabitCategory, Long> {
}