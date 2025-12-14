package com.jangjak.chagok.admin.repository;

import com.jangjak.chagok.admin.dto.DateSuccess;
import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.enums.HabitState;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.jangjak.chagok.habit.entity.QUserAction.userAction;
import static com.jangjak.chagok.habit.entity.QUserHabit.userHabit;

@Repository
@RequiredArgsConstructor
public class UserActionRepositoryImpl implements UserActionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<DateSuccess> findUserActionTimeline(Long userId, LocalDate today) {

        return queryFactory
                .select(Projections.constructor(DateSuccess.class,
                        userAction.actionDate,
                        Expressions.cases()
                                .when(userAction.isCompleted.eq(YN.Y)).then(1)
                                .otherwise(0)
                                .max().eq(1).as("isSuccess")
                ))
                .from(userAction)
                .join(userAction).on(userAction.userHabitId.eq(userHabit.id))

                .where(
                        userHabit.userId.eq(userId),
                        userHabit.state.eq(HabitState.IN_PROGRESS), // 진행 중인 습관만
                        userAction.actionDate.loe(today)             // 오늘 날짜까지의 기록만
                )
                .groupBy(userAction.actionDate)
                .orderBy(userAction.actionDate.desc())
                .fetch();
    }
}
