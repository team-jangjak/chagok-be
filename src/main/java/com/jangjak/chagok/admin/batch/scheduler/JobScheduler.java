package com.jangjak.chagok.admin.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final Job updatePopularCategoriesJob;
    private final Job userHabitStatsJob;
    private final Job streakCalculationJob;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")  // 매일 자정
//    @Scheduled(fixedDelay = 500000) // 테스트용
    public void runJob() throws Exception {
        LocalDate baseDate = LocalDate.now().minusDays(1); // 어제 기준

        // 사용자 수행률 Job
        JobParameters statsParams = new JobParametersBuilder()
                .addString("baseDate", baseDate.toString())
                .addLong("timestamp", System.currentTimeMillis() + 1)
                .toJobParameters();

        jobLauncher.run(userHabitStatsJob, statsParams);

        // 사용자 streak Job
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("JobID", String.valueOf(System.currentTimeMillis()))
            .addString("date", LocalDate.now().toString())
            .toJobParameters();

        jobLauncher.run(streakCalculationJob, jobParameters);

    }


}