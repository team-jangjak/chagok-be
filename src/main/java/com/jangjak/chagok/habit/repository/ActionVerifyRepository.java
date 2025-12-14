package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.ActionVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActionVerifyRepository extends JpaRepository<ActionVerify, Long> {
    Optional<ActionVerify> getActionVerifyByUserActionId(Long userActionId);
}
