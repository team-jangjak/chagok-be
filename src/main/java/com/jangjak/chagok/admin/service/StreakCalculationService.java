package com.jangjak.chagok.admin.service;

import com.jangjak.chagok.admin.dto.DateSuccess;
import com.jangjak.chagok.admin.entity.Streak;
import com.jangjak.chagok.admin.repository.UserActionQueryRepository;
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

    private final UserActionQueryRepository userActionQueryRepository;
    private final UserStreakRepository userStreakRepository;

    // 배치 Processor에서 호출
    // streak 전체 계산: 현재는 사용하지 않지만 혹시 몰라 두겠습니다..
    public Streak allCalculate(Long userId, LocalDate batchDate) {
        log.info("userId: {}", userId);
        // 현재 UserStreak 정보
        Streak currentStreak = userStreakRepository.findById(userId)
                .orElseGet(() -> new Streak(userId, 0, YN.N, null,null));

        // 유저의 액션 타임라인 조회
        List<DateSuccess> timeline = userActionQueryRepository.findUserActionTimeline(userId, batchDate);

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

        // 어제의 성공/실패 여부, Freeze 처리
        int finalStreak = naturalStreak;
        YN finalIsFreeze = currentStreak.getIsFreeze();

        boolean isRequiredAction = !timeline.isEmpty() && timeline.get(0).getActionDate().isEqual(batchDate);

        // 어제가 actionDate라면
        if (isRequiredAction) {
            // 실패일 경우
            if (!timeline.get(0).isSuccess()) {
                if (currentStreak.getIsFreeze() == YN.Y) {
                    finalStreak = currentStreak.getStreak();
                } else {
                    finalStreak = 0;
                }
                finalIsFreeze = YN.N;
            }
        }

        // 최종 결과 업데이트
        currentStreak.updateStreak(finalStreak, finalIsFreeze);
        return currentStreak;
    }

    @Transactional
    public void updateStreak(Long userId, LocalDate date) {
        Streak streak = userStreakRepository.findById(userId)
            .orElseGet(() -> new Streak(userId, 0, YN.N, null, null));

        // 해당 date의 전체 성공 여부 확인
        Optional<DateSuccess> status = userActionQueryRepository.findUserActionStatusByDate(userId, date);

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
