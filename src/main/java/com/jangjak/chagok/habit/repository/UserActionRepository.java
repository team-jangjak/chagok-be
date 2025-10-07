package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}
