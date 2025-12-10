package com.jangjak.chagok.admin.batch.writer;

import com.jangjak.chagok.admin.dto.info.HabitProgressDto;
import com.jangjak.chagok.admin.entity.UserHabitStats;
import com.jangjak.chagok.admin.repository.UserHabitStatsQueryRepository;
import com.jangjak.chagok.habit.enums.HabitState;
import com.jangjak.chagok.user.entity.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserHabitStatsItemWriter implements ItemWriter<User> {

    private final UserHabitStatsQueryRepository statsQueryRepository;
    private final LocalDate baseDate;
    private final JpaItemWriter<UserHabitStats> jpaItemWriter;

    public UserHabitStatsItemWriter(UserHabitStatsQueryRepository statsQueryRepository,
                                    LocalDate baseDate,
                                    EntityManagerFactory emf) {
        this.statsQueryRepository = statsQueryRepository;
        this.baseDate = baseDate;
        this.jpaItemWriter = new JpaItemWriterBuilder<UserHabitStats>()
                .entityManagerFactory(emf)
                .usePersist(false)  // merge 사용
                .build();
    }

    @Override
    public void write(Chunk<? extends User> chunk) {

        List<? extends User> users = chunk.getItems();
        if (users.isEmpty()) return;

        List<Long> userIds = users.stream()
                .map(User::getUserId)
                .toList();

        // 현재 chunk 유저들에 대한 집계 한 번에
        List<HabitProgressDto> userResDto =
                statsQueryRepository.findHabitStatsByUserIds(userIds, baseDate);

        if (userResDto.isEmpty()) return;

        Map<Long, List<HabitProgressDto>> grouped =
                userResDto.stream().collect(Collectors.groupingBy(HabitProgressDto::getUserId));

        List<UserHabitStats> userHabitStatsList = new ArrayList<>();

        for (User user : users) {
            List<HabitProgressDto> progressOfUser = grouped.get(user.getUserId());
            if (progressOfUser == null || progressOfUser.isEmpty()) continue;

            int habitCount = progressOfUser.size();

            double avgProgress = progressOfUser.stream()
                    .mapToDouble(r -> {
                        long expected = (r.getHabitState() == HabitState.COMPLETED)
                                ? isNull(r.getTotalCount()) : isNull(r.getUntilBaseCount());
                        long completed = (r.getHabitState() == HabitState.COMPLETED)
                                ? isNull(r.getTotalCompleted()) : isNull(r.getUntilBaseCompleted());

                        return expected == 0 ? 0 : ((double) completed / expected) * 100;
                    })
                    .average()
                    .orElse(0.0);

            avgProgress = Math.round(avgProgress * 10) / 10.0;  // 소수점 첫째 자리

            UserHabitStats stats = UserHabitStats.create(user.getUserId(), habitCount, avgProgress);
            userHabitStatsList.add(stats);
        }

        if (!userHabitStatsList.isEmpty()) {
            // Chunk로 변환
            Chunk<UserHabitStats> statsChunk = new Chunk<>(userHabitStatsList);
            // DB에 업데이트
            jpaItemWriter.write(statsChunk);
        }
    }

    private long isNull(Long v) {
        return v == null ? 0L : v;
    }
}
