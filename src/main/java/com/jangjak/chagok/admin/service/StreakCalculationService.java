package com.jangjak.chagok.admin.service;

import com.jangjak.chagok.admin.dto.DateSuccess;
import com.jangjak.chagok.admin.entity.UserStreak;
import com.jangjak.chagok.admin.repository.UserActionRepositoryImpl;
import com.jangjak.chagok.admin.repository.UserStreakRepository;
import com.jangjak.chagok.common.enums.YN;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreakCalculationService {

    private final UserActionRepositoryImpl userActionImpl;
    private final UserStreakRepository userStreakRepository;

    // 배치 Processor에서 호출
    public UserStreak calculate(Long userId, LocalDate batchDate) {
        log.info("userId: {}", userId);
        // 현재 UserStreak 정보
        UserStreak currentStreak = userStreakRepository.findById(userId)
                .orElseGet(() -> new UserStreak(userId, 0, YN.N, null));

        // 유저의 액션 타임라인 조회
        List<DateSuccess> timeline = userActionImpl.findUserActionTimeline(userId, batchDate);

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

        boolean isRequiredActionToday = !timeline.isEmpty() && timeline.get(0).actionDate().isEqual(batchDate);

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
            } else {
                // 오늘 성공
                finalStreak = naturalStreak;
                finalIsFreeze = YN.N;
            }
        } else {
            // 오늘 액션 없음 → streak 계산 반영, freeze 유지
            finalStreak = naturalStreak;
            finalIsFreeze = currentStreak.getIsFreeze();
        }

        currentStreak.updateStreak(finalStreak, finalIsFreeze);
        return currentStreak;
    }
}
