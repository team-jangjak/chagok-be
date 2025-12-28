package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    @Query(
            value = """
                        SELECT *
                        FROM action
                        WHERE valid_end_at > :now 
                        AND valid_start_at <= :now 
                        AND habit_id = :habitId
                    """,
            nativeQuery = true
    )
    List<Action> getActionsByHabitId(
            @Param("now") LocalDateTime now,
            @Param("habitId") Long habitId
    );

    boolean existsByCheckMethodId(Long checkMethodId);

    @Modifying
    @Query(
            value = """
                        UPDATE action
                        SET valid_end_at = :validStDt
                        WHERE habit_id = :habitId
                        AND valid_end_at = :max
                    """, nativeQuery = true
    )
    void expireActions(
            @Param("habitId") Long habitId,
            @Param("validStDt") LocalDateTime validStDt,
            @Param("max") LocalDateTime max
    );
}
