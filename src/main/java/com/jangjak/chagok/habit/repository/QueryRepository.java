package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.dto.value.CalendarInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.CheckMethod;
import com.jangjak.chagok.habit.entity.CheckMethodDetail;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitState;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jangjak.chagok.habit.entity.QCheckMethod.checkMethod;
import static com.jangjak.chagok.habit.entity.QCheckMethodDetail.checkMethodDetail;
import static com.jangjak.chagok.habit.entity.QUserHabit.userHabit;
import static com.jangjak.chagok.habit.entity.QUserAction.userAction;
import static com.jangjak.chagok.habit.entity.QAction.action;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueryRepository {

    private final JPAQueryFactory factory;

    /**
     * 특정 상태의 userHabit 조회
     */
    public List<UserHabit> findByUserIdAndStates(Long id, List<HabitState> habitStates) {
        return factory
                .selectFrom(userHabit)
                .where(
                        userHabit.userId.eq(id),
                        userHabit.state.in(habitStates)
                )
                .fetch();
    }

    /**
     * 캘린더 조회
     */
    public List<CalendarInfo> findCalendarInfo(
            Long userId, LocalDate startDate, LocalDate endDate, Long userHabitId, HabitState state
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 유저 필터
        builder.and(userHabit.userId.eq(userId));

        // 날짜 범위
        builder.and(userAction.actionDate.goe(startDate));
        builder.and(userAction.actionDate.lt(endDate));


        // 필터: 특정 userHabit만
        if (userHabitId != null) {
            builder.and(userHabit.userHabitId.eq(userHabitId));
        }

        // 기본 상태: 진행중+완료
        if (state == null) {
            builder.and(userHabit.state.in(HabitState.IN_PROGRESS, HabitState.COMPLETED));
        } else {
            // 필터: 특정 상태만 (진행중/완료)
            builder.and(userHabit.state.eq(state));
        }

        return factory
                .select(Projections.constructor(
                        CalendarInfo.class,
                        userAction.actionDate,
                        userAction.userHabitId,
                        userAction.userActionId,
                        action.content,
                        userAction.isCompleted
                ))
                .from(userAction)
                .join(action).on(action.id.actionId.eq(userAction.actionId))
                .join(userHabit).on(userHabit.userHabitId.eq(userAction.userHabitId))
                .where(builder)
                .orderBy(
                        userAction.actionDate.asc(),
                        userAction.userHabitId.asc(),
                        userAction.userActionId.asc()
                )
                .fetch();
    }

    public Optional<Action> findByActionIdAndCreatedAt(Long actionId, LocalDateTime createdAt) {
        return Optional.ofNullable(
                factory
                        .selectFrom(action)
                        .where(
                                action.id.actionId.eq(actionId),
                                action.validStartAt.loe(createdAt),
                                action.id.validEndAt.goe(createdAt)
                        )
                        .fetchOne()
        );
    }

    public Optional<CheckMethod> findByCheckMethodIdAndCreatedAt(Long checkMethodId, LocalDateTime createdAt) {
        return Optional.ofNullable(
                factory
                        .selectFrom(checkMethod)
                        .where(
                                checkMethod.id.checkMethodId.eq(checkMethodId),
                                checkMethod.validStartAt.loe(createdAt),
                                checkMethod.id.validEndAt.goe(createdAt)
                        )
                        .fetchOne()
        );
    }

    public List<CheckMethodDetail> findDetailsByCheckMethodIdAndCreatedAt(Long checkMethodId, LocalDateTime createdAt) {
        return factory
                .selectFrom(checkMethodDetail)
                .where(
                        checkMethodDetail.id.checkMethodId.eq(checkMethodId),
                        checkMethodDetail.validStartAt.loe(createdAt),
                        checkMethodDetail.id.validEndAt.goe(createdAt)
                )
                .orderBy(checkMethodDetail.id.methodOrder.asc())
                .fetch();
    }

}
