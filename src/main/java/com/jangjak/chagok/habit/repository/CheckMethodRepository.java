package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.CheckMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CheckMethodRepository extends JpaRepository<CheckMethod, Long> {
    List<CheckMethod> getCheckMethodsByIdIn(Collection<Long> ids);
}
