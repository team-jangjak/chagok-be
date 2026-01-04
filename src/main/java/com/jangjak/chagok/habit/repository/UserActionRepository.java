package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.dto.value.ActionAndUserActionView;
import com.jangjak.chagok.habit.dto.value.ProgressRateInfo;
import com.jangjak.chagok.habit.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jangjak.chagok.common.enums.YN;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    /**
     * 사용자가 진행하고 있는 습관의 가장 가까운 action 조회
     *
     * @param userHabitIds
     * @return
     */
    @Query(value = """
            WITH base AS (
                SELECT 
                    ua.user_action_id,
                    ua.user_habit_id,
                    ua.action_date,
                    ua.delay_count,
                    ua.created_at,
                    a."sequence"       AS action_sequence,
                    a."freq_seq"       AS action_freq_seq,
                    a."content"        AS action_content,
                    ROW_NUMBER() OVER (
                        PARTITION BY ua.user_habit_id
                        ORDER BY ua.action_date ASC, a."sequence" ASC, ua.user_action_id ASC
                    ) AS rn
                FROM user_action ua
                JOIN "action" a ON a.action_id = ua.action_id
                AND a.valid_start_at <= ua.created_at
                AND ua.created_at < a.valid_end_at
                WHERE ua.user_habit_id IN (:userHabitIds)
                  AND ua.is_completed = 'N'
                  AND ua.action_date >= CURRENT_DATE
            )
            SELECT
                b.user_action_id,
                b.user_habit_id,
                b.action_content,
                b.action_sequence,
                b.action_freq_seq,
                b.action_date,
                b.delay_count
            FROM base b
            WHERE b.rn = 1
            """, nativeQuery = true)
    List<ActionAndUserActionView> findNextUpcomingPerUserHabit(@Param("userHabitIds") List<Long> userHabitIds);

    /**
     * 습관의 진행률 조회 (총 횟수 + 완료 횟수만 반환)
     *
     * @param userHabitIds
     * @return
     */
    @Query("""
            SELECT new com.jangjak.chagok.habit.dto.value.ProgressRateInfo(
                ua.userHabitId,
                COUNT(ua),
                SUM(CASE WHEN ua.isCompleted = :isCompleted THEN 1 ELSE 0 END)
            )
            FROM UserAction ua
            WHERE ua.userHabitId IN :userHabitIds
            GROUP BY ua.userHabitId
            """)
    List<ProgressRateInfo> findProgressRates(@Param("userHabitIds") List<Long> userHabitIds, @Param("isCompleted") YN isCompleted);


    int countByUserHabitId(Long userHabitId);

    int countByUserHabitIdAndIsCompleted(Long userHabitId, YN isCompleted);

}
