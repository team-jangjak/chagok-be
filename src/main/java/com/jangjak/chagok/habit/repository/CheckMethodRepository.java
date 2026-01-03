package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.CheckMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface CheckMethodRepository extends JpaRepository<CheckMethod, Long> {
    List<CheckMethod> getCheckMethodsByIdCheckMethodIdIn(Collection<Long> ids);

    @Modifying
    @Query(value = """
        UPDATE check_method
        SET valid_end_at = :validStDt
        WHERE check_method_id = :checkMethodId
        AND valid_end_at = :max
    """, nativeQuery = true)
    void expireCheckMethod(Long checkMethodId, LocalDateTime validStDt, LocalDateTime max);
}
