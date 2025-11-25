package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.ActionVerify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionVerifyRepository extends JpaRepository<ActionVerify, Long> {
    boolean existsById(Long id);
}
