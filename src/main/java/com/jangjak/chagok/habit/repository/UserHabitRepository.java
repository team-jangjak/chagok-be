package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHabitRepository extends JpaRepository<UserHabit, Long> {
    List<UserHabit> findByUserIdAndState(Long id, HabitState habitState);
}
