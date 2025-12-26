package com.jangjak.chagok.admin.batch.config;

import com.jangjak.chagok.admin.entity.UserStreak;
import com.jangjak.chagok.admin.repository.UserStreakRepository;
import com.jangjak.chagok.admin.service.StreakCalculationService;
import com.jangjak.chagok.habit.enums.HabitState;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class StreakCalculationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final StreakCalculationService streakCalculationService;
    private final UserStreakRepository userStreakRepository;

    private static final int CHUNK_SIZE = 10;

    // Job: Step 실행
    @Bean
    public Job streakCalculationJob() {
        return new JobBuilder("streakCalculationJob", jobRepository)
                .start(streakCalculationStep())
                .build();
    }

    // Step: Reader, Processor, Writer 연결
    @Bean
    public Step streakCalculationStep() {
        return new StepBuilder("streakCalculationStep", jobRepository)
                .<Long, UserStreak>chunk(CHUNK_SIZE, transactionManager)
                .reader(userReader())
                .processor(streakProcessor())
                .writer(userStreakWriter())
                .build();
    }

    // Reader: IN_PROGRESS 상태인 UserHabit을 가진 유저 ID 목록을 페이징하여 읽기
    @Bean
    public JpaPagingItemReader<Long> userReader() {
        String jpql = "SELECT DISTINCT uh.userId FROM UserHabit uh WHERE uh.state = :state";

        JpaPagingItemReader<Long> reader = new JpaPagingItemReaderBuilder<Long>()
                .name("userReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString(jpql)
                .parameterValues(Map.of("state", HabitState.IN_PROGRESS))
                .build();

        return reader;
    }

    // Processor: userId별로 Streak 계산
    @Bean
    public ItemProcessor<Long, UserStreak> streakProcessor() {
        // 어제 기준으로 실패한 사람들을 찾아 초기화
        return userId -> {
            streakCalculationService.updateStreak(userId, LocalDate.now().minusDays(1));
            return userStreakRepository.findById(userId).orElse(null);
        };
    }

    // Writer: UserStreak 테이블에 INSERT or UPDATE
    @Bean
    public JpaItemWriter<UserStreak> userStreakWriter() {
        JpaItemWriter<UserStreak> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

}
