package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.dto.DateSuccess;

import java.time.LocalDate;
import java.util.List;

public interface UserActionRepositoryCustom {
    List<DateSuccess> findUserActionTimeline(Long userId, LocalDate today);
}
