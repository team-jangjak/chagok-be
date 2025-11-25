package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    List<Action> getActionsByHabitId(Long habitId);
    boolean existsByCheckMethodId(Long checkMethodId);
}
