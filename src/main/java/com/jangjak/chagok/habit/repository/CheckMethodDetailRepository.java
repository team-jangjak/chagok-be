package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.CheckMethodDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckMethodDetailRepository extends JpaRepository<CheckMethodDetail, Long> {
    List<CheckMethodDetail> findByIdCheckMethodIdOrderByIdMethodOrderAsc(Long checkMethodId);


    @Modifying
    @Query(value = """
                UPDATE check_method_detail
                SET valid_end_at = :validStDt
                WHERE check_method_id = :checkMethodId
                AND valid_end_at = :max
            """, nativeQuery = true)
    void expireCheckMethodDetail(Long checkMethodId, LocalDateTime validStDt, LocalDateTime max);
}
