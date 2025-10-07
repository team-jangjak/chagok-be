package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}
