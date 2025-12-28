package com.jangjak.chagok.habit.service.update;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.external.service.s3.S3ImageService;
import com.jangjak.chagok.habit.dto.request.update.HabitUpdateRequestDto;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.Habit;
import com.jangjak.chagok.habit.mapper.ActionMapper;
import com.jangjak.chagok.habit.mapper.HabitMapper;
import com.jangjak.chagok.habit.repository.HabitQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitUpdateService {
    private final HabitQuery habitQuery;
    private final S3ImageService s3ImageService;

    @Transactional
    public Long updateHabit(Long userId, HabitUpdateRequestDto reqDto) {
        LocalDateTime validStDt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 기존 습관 조회 및 만료
        // TODO : 내가 만든 습관이 아니라면 수정이 불가능해야함.
        Habit existing = habitQuery.getHabitById(reqDto.getId());
        habitQuery.expireHabit(existing.getId().getHabitId(), validStDt);

        // 이미지 관련 변경사항 처리
        if (reqDto.getHabitImage() != null) {
            // 새로운 이미지 등록
            String imageUrl = s3ImageService.registerImage(reqDto.getHabitImage());

            // 기존 이미지 삭제
            s3ImageService.deleteImage(existing.getImage());

            reqDto.setHabitImage(imageUrl);
        }

        // 업데이트 된 습관 저장
        Habit habit = HabitMapper.updateFrom(existing, reqDto, validStDt);
        habitQuery.saveHabit(habit);

        // 행위 변경 사항이 없고 빈도가 변경되지 않았다면 행위 변경 X
        if (reqDto.getActions() == null
                && reqDto.getFreqUnit() == null
                && reqDto.getFreqCount() == null
                && reqDto.getFrequency() == null) return existing.getId().getHabitId();

        if (reqDto.getActions() == null || reqDto.getActions().isEmpty()) {
            log.error("습관의 빈도값이 변경되었음에도 행위 요청값이 수신되지 않음.");
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 기존 행위 만료
        habitQuery.expireActions(existing.getId().getHabitId(), validStDt);

        // 업데이트 된 행위 저장
        List<Action> actions = ActionMapper.updateFrom(reqDto.getId(), reqDto, validStDt);
        habitQuery.saveActionList(actions);

        return existing.getId().getHabitId();
    }
}
