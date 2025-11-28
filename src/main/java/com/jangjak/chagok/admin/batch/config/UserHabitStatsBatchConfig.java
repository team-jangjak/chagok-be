package com.jangjak.chagok.admin.batch.config;

import com.jangjak.chagok.admin.repository.UserHabitStatsQueryRepository;
import com.jangjak.chagok.admin.batch.writer.UserHabitStatsItemWriter;
import com.jangjak.chagok.user.entity.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class UserHabitStatsBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final UserHabitStatsQueryRepository statsRepository;

    /**
     * 유저별 습관 수행률(어제 기준)을 집계하는 Job
     */
    @Bean
    public Job userHabitStatsJob(Step userHabitStatsStep) {
        return new JobBuilder("userHabitStatsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(userHabitStatsStep)
                .build();
    }

    /**
     * User를 chunk 단위로 읽고 → chunk 안의 userId로 한 번에 집계 → user_habit_stats 에 저장
     */
    @Bean
    public Step userHabitStatsStep(JpaPagingItemReader<User> userItemReader,
                                   ItemWriter<User> itemWriter) {

        return new StepBuilder("userHabitStatsStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(userItemReader)
                .writer(itemWriter)
                .build();
    }

    /**
     * User 테이블을 10명씩 끊어서 Step으로 공급
     */
    @Bean
    public JpaPagingItemReader<User> userItemReader() {
        return new JpaPagingItemReaderBuilder<User>()
                .name("userItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10) // chunk 사이즈와 동일
                .queryString("SELECT u FROM User u")   // 조건 필요하면 추가
                .build();
    }

    /**
     * JobParameter(baseDate)를 주입받음
     * 각 chunk의 User 리스트를 가지고 UserHabitStats 계산 후 저장
     */
    @Bean
    @StepScope // Step 실행 시점에 Bean을 생성
    public ItemWriter<User> userHabitStatsItemWriter(
            @Value("#{jobParameters['baseDate']}") String baseDateStr
    ) {
        LocalDate baseDate = LocalDate.parse(baseDateStr); // "2025-11-26" 형식
        return new UserHabitStatsItemWriter(statsRepository, baseDate, entityManagerFactory);
    }
}