package com.jangjak.chagok.external.service.s3;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.external.dto.request.ImageUploadRequestDto;
import com.jangjak.chagok.external.enums.ImageUploadType;
import com.jangjak.chagok.external.util.s3.S3UtilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3PutService {
    private final S3Client s3Client;
    private final S3UtilityService s3Util;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    /**
     * <pre>
     *     새로운 S3 Image 업로드 (검증 과정 포함)
     *     1. 카테고리 입력값 검증
     *     2. 이미지가 진짜 이미지인지 검증
     *     3. PUT 요청
     * </pre>
     */
    public String putImage(MultipartFile image, ImageUploadRequestDto meta, boolean isTempImg) {
        log.info("이미지 업로드 서비스 시작");

        // 0. 카테고리 입력값 검증
        if (ImageUploadType.fromValue(meta.getType()) == ImageUploadType.NONE) {
            log.error("유효하지 않은 카테고리 입력입니다. | category: {}", meta.getType());
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 1. image가 없거나 이미지 이름이 없다면 오류
        if (image.isEmpty() || image.getOriginalFilename() == null) {
            log.error("빈 이미지가 요청되었습니다.");
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        String name = image.getOriginalFilename();

        // 2. 이미지가 진짜 이미지인지 검증
        try {
            s3Util.checkImageValidation(image, name);
        } catch (IOException e) {
            log.error("이미지를 바이트배열로 변환하는 과정에서 오류가 발생했습니다.");
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("알수 없는 오류가 발생하였습니다.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 3. PUT 요청
        try {
            return putImageToS3(image, meta, name, isTempImg);
        } catch (Exception e) {
            log.error("이미지 업로드 과정에서 알 수 없는 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * S3 PUT 요청
     */
    private String putImageToS3(MultipartFile image, ImageUploadRequestDto meta, String name, boolean isTempImg) throws IOException {
        // PUT 요청 생성
        String fileName = s3Util.getFileName(name, meta, isTempImg);
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .metadata(s3Util.getMetaData(name))
                .contentType(image.getContentType())
                .contentDisposition("inline")
                .expires(Instant.now().plus(10L, ChronoUnit.MINUTES))
                .build();

        // Request Body 생성
        RequestBody requestBody = RequestBody.fromInputStream(
                image.getInputStream(),
                image.getSize()
        );

        // S3 PUT 요청
        PutObjectResponse response = s3Client.putObject(putRequest, requestBody);
        log.info(response.toString());

        // 이미지 url 반환
        return s3Util.returnImageUrl(fileName);

    }
}