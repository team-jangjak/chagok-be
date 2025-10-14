package com.jangjak.chagok.habit.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularCategoryScheduler {

    private final JobLauncher jobLauncher;
    private final Job updatePopularCategoriesJob;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정
//    @Scheduled(fixedDelay = 10000) // 테스트용
    public void runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis()) // 중복 실행 방지
                .toJobParameters();
        jobLauncher.run(updatePopularCategoriesJob, params);
    }
}