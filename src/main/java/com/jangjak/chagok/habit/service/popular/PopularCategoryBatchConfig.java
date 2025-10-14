package com.jangjak.chagok.habit.service.popular;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PopularCategoryBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job updatePopularCategoriesJob() {
        return new JobBuilder("updatePopularCategoriesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updatePopularCategoriesStep())
                .build();
    }

    @Bean
    public Step updatePopularCategoriesStep() {
        return new StepBuilder("updatePopularCategoriesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // 1. 기존 통계 비우기
                    jdbcTemplate.update("TRUNCATE TABLE public.popular_habit_category");

                    // 2. 집계해서 바로 INSERT
                    jdbcTemplate.update("""
                            WITH category_counts AS (
                              SELECT h.category_id, COUNT(uh.id) AS usage_count
                              FROM user_habit uh
                              JOIN habit h ON h.id = uh.habit_id
                              -- 공개만 집계
                              -- WHERE h.is_public = 'Y'
                              GROUP BY h.category_id
                            )
                            INSERT INTO public.popular_habit_category (category_id, usage_count, calculated_at)
                            SELECT category_id, usage_count, CURRENT_TIMESTAMP
                            FROM category_counts
                            ORDER BY usage_count DESC, category_id ASC
                            LIMIT 5
                            """);

                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }
}
