package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.CheckMethodDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CheckMethodDetailRepository extends JpaRepository<CheckMethodDetail, Long> {

    @Modifying
    @Query(
            value = """
                        UPDATE check_method_detail
                        SET valid_end_at = :validStDt
                        WHERE check_method_id = :checkMethodId
                        AND valid_end_at = :max
                    """, nativeQuery = true
    )
    void expireCheckMethodDetail(
            @Param("checkMethodId") Long checkMethodId,
            @Param("validStDt") LocalDateTime validStDt,
            @Param("max") LocalDateTime max
    );
}
