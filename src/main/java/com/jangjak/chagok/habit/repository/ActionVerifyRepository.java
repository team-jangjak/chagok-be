package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.ActionVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionVerifyRepository extends JpaRepository<ActionVerify, Long> {
    Boolean existsByUserActionId(Long userActionId);
    List<ActionVerify> findAllByUserActionId(Long userActionId);
}
