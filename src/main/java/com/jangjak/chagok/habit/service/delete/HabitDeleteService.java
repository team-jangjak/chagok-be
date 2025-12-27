package com.jangjak.chagok.habit.service.delete;

import com.jangjak.chagok.habit.dto.request.delete.HabitDeleteRequestDto;
import com.jangjak.chagok.habit.dto.request.update.HabitUpdateRequestDto;
import com.jangjak.chagok.habit.repository.HabitQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitDeleteService {
    private final HabitQuery habitQuery;

    @Transactional
    public void deleteHabit(Long userId, HabitDeleteRequestDto reqDto) {
        LocalDateTime validStDt = LocalDateTime.now();
        Long habitId = reqDto.getId();

        // 습관 만료
        // TODO : 본인의 습관이 맞는 지 확인
        // 이미지는 템플릿 습관의 경우 삭제가 진행되도 다른 이가 사용할 수 있기 때문에 남겨두어야 함.
        habitQuery.expireHabit(habitId, validStDt);

        // 행위 만료
        habitQuery.expireActions(habitId, validStDt);
    }
}
