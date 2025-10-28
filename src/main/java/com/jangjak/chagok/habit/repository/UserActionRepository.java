package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.dto.response.ActionAndUserActionView;
import com.jangjak.chagok.habit.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Query(value = """
            SELECT *
            FROM (
                SELECT 
                    ua.id AS userActionId,
                    ua.action_date AS actionDate,
                    ua.action_id AS actionId,
                    ua.is_completed AS isCompleted,
                    ua.user_habit_id AS userHabitId,
                    ua.delay_count AS delayCount,
                    a.habit_id AS habitId,
                    a."sequence" AS actionSequence,
                    a."freq_seq" AS actionFreqSeq,
                    a."content" AS actionContent,
                    a."check_method_id" AS checkMethodId,
            
                    ROW_NUMBER() OVER (
                        PARTITION BY ua.user_habit_id
                        ORDER BY ua.action_date ASC, a."sequence" ASC, ua.id ASC
                    ) AS rank
                FROM user_action ua
                JOIN "action" a ON a.id = ua.action_id
                WHERE ua.user_habit_id IN (:userHabitIds)
                  AND ua.is_completed = 'N'
                  AND ua.action_date >= CURRENT_DATE
            ) t
            WHERE t.rank = 1
            """, nativeQuery = true)
    List<ActionAndUserActionView> findNextUpcomingPerUserHabit(
            @Param("userHabitIds") List<Long> userHabitIds,
            @Param("habitIds") List<Long> habitIds
    );
}
