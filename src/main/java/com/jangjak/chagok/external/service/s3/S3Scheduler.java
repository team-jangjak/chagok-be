package com.jangjak.chagok.external.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Scheduler {
    private final S3Client s3Client;
    private final S3DeleteService s3DeleteService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시 실행
    public void cleanupTempFiles() {
        try {
            // 제한시간 설정
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(60);

            // S3 temp 폴더 내 모든 사진 가져오기
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix("temp/")
                    .build();

            // 응답 객체
            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            // 마지막 수정시간 기준
            response.contents().stream()
                    .filter(obj -> obj.lastModified().isBefore(cutoffTime.toInstant(ZoneOffset.UTC)))
                    .forEach(obj -> s3DeleteService.deleteS3Image(obj.key()));
        } catch (Exception e) {
            log.error("Failed to cleanup temp files", e);
        }
    }
}
