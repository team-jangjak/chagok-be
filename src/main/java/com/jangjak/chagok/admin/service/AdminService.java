package com.jangjak.chagok.admin.service;

import com.jangjak.chagok.admin.dto.res.GlobalProgressResDto;
import com.jangjak.chagok.admin.dto.res.UserProgressResDto;
import com.jangjak.chagok.admin.entity.UserHabitStats;
import com.jangjak.chagok.admin.repository.AdminRepository;
import com.jangjak.chagok.admin.repository.UserHabitStatsQueryRepository;
import com.jangjak.chagok.admin.repository.UserHabitStatsRepository;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.user.entity.User;
import com.jangjak.chagok.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserHabitStatsRepository userHabitStatsRepository;
    private final UserHabitStatsQueryRepository userHabitStatsQueryRepository;
    private final AdminRepository adminRepository;

    public GlobalProgressResDto getAllProgress(Long id) {
        Double avg = userHabitStatsQueryRepository.findGlobalAverageProgress();
        return GlobalProgressResDto.builder().avgProgress(Math.round(avg * 10) / 10.0).build();
    }


    @Transactional(readOnly = true)
    public Page<UserProgressResDto> getUsersProgress(Long id, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // 유저 페이징 조회
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> users = userPage.getContent();

        if (users.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> userIdList = users.stream()
                .map(User::getId)
                .toList();

        // UserHabitStats 테이블에서 userIdList에 해당하는 데이터 조회
        List<UserHabitStats> statsList = userHabitStatsRepository.findByUserIdIn(userIdList);

        // userId -> stats 매핑 맵 생성
        Map<Long, UserHabitStats> statsMap = statsList.stream()
                .collect(Collectors.toMap(
                        UserHabitStats::getUserId,
                        stats -> stats
                ));

        // User + Stats 합쳐서 resDto 리스트 생성
        List<UserProgressResDto> content = users.stream()
                .map(user -> {
                    UserHabitStats stats = statsMap.get(user.getId());

                    Double avgProgress = (stats != null) ? stats.getAvgProgress() : 0.0;
                    Integer habitCount = (stats != null) ? stats.getHabitCount() : 0;
                    LocalDateTime calculatedAt = (stats != null) ? stats.getCalculatedAt() : null;

                    return UserProgressResDto.builder()
                            .id(user.getId()) // userId
                            .name(user.getName())
                            .avgProgress(avgProgress)
                            .habitCount(habitCount)
                            .calculatedAt(calculatedAt)
                            .build();
                })
                .toList();

        return new PageImpl<>(content, pageable, userPage.getTotalElements());
    }

    private void validate(Long id) {
        if(!adminRepository.existsByIdAndRoleIsAdmin(id)){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

}
