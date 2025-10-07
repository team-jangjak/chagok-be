package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.UserAction;
import com.jangjak.chagok.habit.entity.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHabitRepository extends JpaRepository<UserHabit, Long> {
}
