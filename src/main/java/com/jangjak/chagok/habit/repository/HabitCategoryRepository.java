package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.HabitCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface HabitCategoryRepository extends JpaRepository<HabitCategory, Long> {
    List<HabitCategory> findByIdIn(Set<Long> ids);
}