package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.dto.info.HabitProgressDto;
import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.enums.HabitState;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.jangjak.chagok.admin.entity.QUserHabitStats.userHabitStats;
import static com.jangjak.chagok.habit.entity.QUserAction.userAction;
import static com.jangjak.chagok.habit.entity.QUserHabit.userHabit;

@Repository
@RequiredArgsConstructor
public class UserHabitStatsQueryRepositoryImpl implements UserHabitStatsQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HabitProgressDto> findHabitStatsByUserIds(List<Long> userIds, LocalDate baseDate) {

        // NumberExpression<Long>: Long 타입의 숫자 연산을 나타내는 QueryDSL 표현식
        // is_completed = 'Y'
        NumberExpression<Long> totalCompleted =
                new CaseBuilder()
                        .when(userAction.isCompleted.eq(YN.Y)).then(1L)
                        .otherwise(0L);

        // baseDate까지 예정된 action 개수
        NumberExpression<Long> untilBaseCount =
                new CaseBuilder()
                        .when(userAction.actionDate.loe(baseDate)).then(1L)
                        .otherwise(0L);

        // baseDate까지 완료된 action 수
        NumberExpression<Long> untilBaseCompleted =
                new CaseBuilder()
                        .when(userAction.actionDate.loe(baseDate)
                                .and(userAction.isCompleted.eq(YN.Y)))
                        .then(1L)
                        .otherwise(0L);

        return queryFactory
                .select(com.querydsl.core.types.Projections.constructor(
                        HabitProgressDto.class,
                        userHabit.id,                    // userHabitId
                        userHabit.userId,               // userId
                        userHabit.state,                 // habitState
                        userAction.id.count(),           // totalCount
                        totalCompleted.sum(),        // totalCompleted
                        untilBaseCount.sum(),        // untilBaseCount
                        untilBaseCompleted.sum()     // untilBaseCompleted
                ))
                .from(userAction)
                .join(userHabit).on(userHabit.id.eq(userAction.userHabitId))
                .where(
                        userHabit.userId.in(userIds),
                        userHabit.state.in(HabitState.IN_PROGRESS, HabitState.COMPLETED)
                )
                .groupBy(
                        userHabit.id,
                        userHabit.userId,
                        userHabit.state
                )
                .fetch();
    }

    @Override
    public Double findGlobalAverageProgress() {
        Double result = queryFactory
                .select(userHabitStats.avgProgress.avg())
                .from(userHabitStats)
                .where(userHabitStats.habitCount.gt(0))
                .fetchOne();

        return result != null ? result : 0.0;
    }
}
