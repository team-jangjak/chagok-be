package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.CheckMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CheckMethodRepository extends JpaRepository<CheckMethod, Long> {
    List<CheckMethod> getCheckMethodsByIdCheckMethodIdIn(Collection<Long> ids);


    @Query(
            value = """
                        SELECT *
                        FROM check_method 
                        WHERE valid_end_at > :now 
                        AND valid_start_at <= :now 
                        AND check_method_id = :checkMethodId
                    """,
            nativeQuery = true
    )
    Optional<CheckMethod> findByCheckMethodId(
            @Param("now") LocalDateTime now,
            @Param("checkMethodId") Long checkMethodId
    );


    @Modifying
    @Query(
            value = """
                        UPDATE check_method 
                        SET valid_end_at = :validStDt
                        WHERE check_method_id = :checkMethodId
                        AND valid_end_at = :max
                    """, nativeQuery = true
    )
    void expireCheckMethod(
            @Param("checkMethodId") Long checkMethodId,
            @Param("validStDt") LocalDateTime validStDt,
            @Param("max") LocalDateTime max
    );
}
