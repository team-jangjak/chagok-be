package com.jangjak.chagok.habit.batch.config;

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

/**
 * 인기 습관 카테고리 통계를 집계하는 Spring Batch 설정 클래스입니다.
 *
 * 이 배치 작업은 주기적으로 실행되어 `user_habit` 테이블의 데이터를 분석하고,
 * 가장 많이 사용된 상위 5개 습관 카테고리를 `popular_habit_category` 테이블에 업데이트합니다.
 */
@Configuration
@RequiredArgsConstructor
public class PopularCategoryBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;
    private final JdbcTemplate jdbcTemplate;

    /**
     * '인기 카테고리 업데이트' 작업을 정의하는 Job Bean을 생성합니다.
     *
     * @return Job 인스턴스
     */
    @Bean
    public Job updatePopularCategoriesJob() {
        return new JobBuilder("updatePopularCategoriesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                // 이 Job이 실행할 단일 Step을 지정
                .start(updatePopularCategoriesStep())
                .build();
    }

    /**
     * 인기 카테고리 집계 및 저장을 수행하는 Step Bean을 생성합니다.
     *
     * @return Step 인스턴스
     */
    @Bean
    public Step updatePopularCategoriesStep() {
        return new StepBuilder("updatePopularCategoriesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // 1. 기존 통계 비우기
                    // TRUNCATE는 DELETE보다 빠르고 트랜잭션 로그를 적게 사용
                    jdbcTemplate.update("TRUNCATE TABLE public.popular_habit_category");

                    // 2. 집계해서 바로 INSERT
                    // 카테고리별 사용 횟수를 계산하고, 상위 5개를 테이블에 삽입
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
