package com.jangjak.chagok.admin.service;

import com.jangjak.chagok.admin.dto.DateSuccess;
import com.jangjak.chagok.admin.entity.UserStreak;
import com.jangjak.chagok.admin.repository.QueryRepository;
import com.jangjak.chagok.admin.repository.UserStreakRepository;
import com.jangjak.chagok.common.enums.YN;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreakCalculationService {

    private final QueryRepository queryRepository;
    private final UserStreakRepository userStreakRepository;

    // 배치 Processor에서 호출
    // 전체 계산
    public UserStreak allCalculate(Long userId, LocalDate batchDate) {
        log.info("userId: {}", userId);
        // 현재 UserStreak 정보
        UserStreak currentStreak = userStreakRepository.findById(userId)
                .orElseGet(() -> new UserStreak(userId, 0, YN.N, null,null));

        // 유저의 액션 타임라인 조회
        List<DateSuccess> timeline = queryRepository.findUserActionTimeline(userId, batchDate);

        // TODO 수정해야함 (freeze 사용 여부 확인)
        // Streak 계산 (최근 날짜부터 연속 성공 횟수)
        int naturalStreak = 0;
        for (DateSuccess ds : timeline) {
            if (ds.isSuccess()) {
                naturalStreak++;
            } else {
                break; // 실패하는 순간 연속 기록 끊김
            }
        }

        // 오늘의 성공/실패 여부, Freeze 처리
        int finalStreak = naturalStreak;
        YN finalIsFreeze = currentStreak.getIsFreeze();

        boolean isRequiredActionToday = !timeline.isEmpty() && timeline.get(0).getActionDate().isEqual(batchDate);

        // 오늘이 필수 액션 날짜라면
        if (isRequiredActionToday) {
            boolean todayFailed = !timeline.get(0).isSuccess();

            if (todayFailed) {
                if (currentStreak.getIsFreeze() == YN.Y) {
                    finalStreak = currentStreak.getStreak();
                    finalIsFreeze = YN.N;
                } else {
                    finalStreak = 0;
                    finalIsFreeze = YN.N;
                }
            }
        }

        // 최종 결과 업데이트
        currentStreak.updateStreak(finalStreak, finalIsFreeze);
        return currentStreak;
    }


    // 일일 계산
    public UserStreak dayCalculate(Long userId, LocalDate today) {
        // 현재 UserStreak 정보
        UserStreak currentStreak = userStreakRepository.findById(userId)
                .orElseGet(() -> new UserStreak(userId, 0, YN.N, null, null));

        // 오늘 액션 현황 조회
        Optional<DateSuccess> todayStatus = queryRepository.findUserActionStatusByDate(userId, today);

        int finalStreak = currentStreak.getStreak();
        YN finalIsFreeze = currentStreak.getIsFreeze();

        if (todayStatus.isPresent()) {
            // CASE 1: 오늘 해야 할 액션이 있는 날
            if (todayStatus.get().isSuccess()) {
                // 성공: 기존 스트릭에 +1
                finalStreak++;
            } else {
                // 실패
                if (currentStreak.getIsFreeze() == YN.Y) {
                    // 방어권 사용: 스트릭 유지, 프리즈 N
                    finalIsFreeze = YN.N;
                } else {
                    // 방어권 미사용: 스트릭 초기화
                    finalStreak = 0;
                }
            }
        }

        // 최종 결과 업데이트
        currentStreak.updateStreak(finalStreak, finalIsFreeze);
        return currentStreak;
    }

    @Transactional
    public void updateStreak(Long userId, LocalDate date) {
        UserStreak streak = userStreakRepository.findById(userId)
            .orElseGet(() -> new UserStreak(userId, 0, YN.N, null, null));

        // 오늘 전체 성공 여부 확인
        Optional<DateSuccess> status = queryRepository.findUserActionStatusByDate(userId, date);

        if (status.isPresent() && status.get().isSuccess()) {
            // 성공: 스트릭 증가
            streak.incrementStreak(date);
        } else if (date.isBefore(LocalDate.now())) {
            // 실패 (배치 전용): 초기화 or freeze
            if (streak.getIsFreeze() == YN.Y) {
                streak.useFreeze();
            } else {
                streak.resetStreak();
            }
        }
    }

}
