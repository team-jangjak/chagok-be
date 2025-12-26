package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStreakRepository extends JpaRepository<Streak, Long> {
}
