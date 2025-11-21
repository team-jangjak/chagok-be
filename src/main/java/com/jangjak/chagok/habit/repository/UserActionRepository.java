package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.dto.value.ActionAndUserActionView;
import com.jangjak.chagok.habit.dto.value.CalendarInfo;
import com.jangjak.chagok.habit.dto.value.ProgressRateInfo;
import com.jangjak.chagok.habit.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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
                    ua.id,
                    ua.user_habit_id,
                    ua.action_id,
                    ua.action_date,
                    ua.is_completed,
                    ua.delay_count,
                    a.habit_id,
                    a."sequence"       AS action_sequence,
                    a."freq_seq"       AS action_freq_seq,
                    a."content"        AS action_content,
                    ROW_NUMBER() OVER (
                        PARTITION BY ua.user_habit_id
                        ORDER BY ua.action_date ASC, a."sequence" ASC, ua.id ASC
                    ) AS rn
                FROM user_action ua
                JOIN "action" a ON a.id = ua.action_id
                WHERE ua.user_habit_id IN (:userHabitIds)
                  AND ua.is_completed = 'N'
                  AND ua.action_date >= CURRENT_DATE
            )
            SELECT
                h.freq_unit                  AS frequencyUnit,
                b.id                         AS userActionId,   
                b.user_habit_id              AS userHabitId,
                h.image                      AS image,
            
                b.action_content             AS actionContent,
                b.action_sequence            AS actionSequence,
                b.action_freq_seq            AS actionFreqSeq,
            
                b.action_date                AS actionDate,
                b.delay_count                AS delayCount
            FROM base b
            JOIN habit h ON h.id = b.habit_id
            WHERE b.rn = 1
            """, nativeQuery = true)
    List<ActionAndUserActionView> findNextUpcomingPerUserHabit(
            @Param("userHabitIds") List<Long> userHabitIds
    );

    /**
     * 습관의 진행률 조회 (총 횟수 + 완료 횟수만 반환)
     * @param userHabitIds
     * @return
     */
    @Query(value = """
            SELECT 
              ua.user_habit_id AS userHabitId,
              COUNT(*) AS totalCount,
              SUM(CASE WHEN ua.is_completed = 'Y' THEN 1 ELSE 0 END) AS completedCount
            FROM user_action ua
            WHERE ua.user_habit_id IN (:userHabitIds)    
            GROUP BY ua.user_habit_id
            """, nativeQuery = true)
    List<ProgressRateInfo> findProgressRates(@Param("userHabitIds") List<Long> userHabitIds);


    // 날짜 범위 + (옵션: 특정 습관들만)
    /**
     * 날짜별 action 조회
     * @param startDate
     * @param endDate
     * @param userHabitIds
     * @return
     */
    @Query(value = """
            SELECT 
                ua.action_date           AS actionDate,
                ua.user_habit_id         AS userHabitId,
                ua.id                    AS userActionId,
                a.content                AS actionContent,
                ua.is_completed          AS isCompleted
            FROM user_action ua
            JOIN "action" a    ON a.id = ua.action_id
            JOIN user_habit uh ON uh.id = ua.user_habit_id
            WHERE 
              ua.action_date >= :startDate
              AND ua.action_date <  :endDate
              AND ( :userHabitIds IS NULL OR ua.user_habit_id IN (:userHabitIds) )  -- userHabitIds가 null이면 모든 습관
            ORDER BY ua.action_date ASC, ua.user_habit_id ASC, ua.id ASC  
            """, nativeQuery = true)
    List<CalendarInfo> findCalendarInfo(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userHabitIds") List<Long> userHabitIds
    );

    /**
     * 습관&행위 상세 조회
     *
     * @param userActionId
     * @return
     */
    @Query(value = """
            SELECT 
                -- habit
                h.title                               AS habitTitle,
                h.frequency                           AS frequency,
                h.freq_unit                           AS frequencyUnit,
                c.name                                AS categoryName,
                h.image                               AS image,
            
                -- action
                a.content                             AS actionContent,
                a.check_method_id                     AS checkMethodId,
                a."sequence"                          AS actionSequence,
                a."freq_seq"                          AS actionFreqSeq,
            
                -- user_action
                ua.id                                 AS userActionId,     -- userActionId
                ua.action_date                        AS actionDate,
                ua.delay_count                        AS delayCount,
                ua.is_completed                       AS isCompleted,
            FROM user_action ua
            JOIN "action" a ON a.id = ua.action_id
            JOIN habit h     ON h.id = a.habit_id
            LEFT JOIN category c ON c.id = h.category_id
            WHERE ua.id = :userActionId
            """, nativeQuery = true)
    ActionAndUserActionView findHabitActionDetail(@Param("userActionId") Long userActionId);

    int countByUserHabitId(Long userHabitId);
    int countByUserHabitIdAndIsCompleted(Long userHabitId, String isCompleted);

}
