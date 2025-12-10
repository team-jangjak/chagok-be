package com.jangjak.chagok.external.util.s3;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.external.dto.request.ImageUploadRequestDto;
import com.jangjak.chagok.external.enums.ImageUploadType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3UtilityService {
    @Value("${cloudfront.alter}")
    private String ALTER_URL;

    private static final Tika tika = new Tika();

    /**
     * 파일 이름 생성 메서드
     *
     * @return temp/category/UUID/name
     */
    public String getFileName(String name, ImageUploadRequestDto meta, boolean isTempImg) {
        String temp = isTempImg ? "temp/" : "";
        ImageUploadType type = ImageUploadType.fromValue(meta.getType());
        if (type.hasIdInUrl()) {
            return new String((temp + type.name() + "/" + meta.getId() + "/" + UUID.randomUUID() + name).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } else {
            return new String((temp + type.name() + "/" + UUID.randomUUID() + name).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
    }

    /**
     * 파일의 metadata 생성 메서드
     *
     * @return {"original-filename" : "name", "upload-time" : "현재 시각"}
     */
    public Map<String, String> getMetaData(String name) {
        return Map.of(
                "original-filename", new String((name).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                "upload-time", Instant.now().toString()
        );
    }

    /**
     * Image URL에서 key 값 분리 메서드
     */
    public String detachImageUrl(String url) {
        if (url.contains("temp/")) return url.replace(ALTER_URL + "temp/", "");
        else return url.replace(ALTER_URL, "");
    }

    public String returnImageUrl(String fileName) {
        return ALTER_URL + fileName;
    }

    /**
     * 이미지 유효성 검증
     * - 확장자와 이미지 파일 형식 비교
     * - 이미지 파일 형식이 허용되는 형식인지 검증
     */
    public void checkImageValidation(MultipartFile image, String name) throws IOException {
        // 파일 이름에서 확장자 가져오기
        String ext = name.substring(name.lastIndexOf('.') + 1);

        // Tika를 통한 이미지 검증
        byte[] imgBytes = image.getBytes();
        String detectType = tika.detect(imgBytes);

        // 감지된 이미지 타입이 허용하는 이미지 타입인지 검사
        if ("webp".equals(detectType)) {
            log.error("허용되지 않는 이미지 타입입니다. | detectType: {}", detectType);
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 확장자와 감지된 이미지 타입이 일치하는지 검사
        if (!Objects.equals(getContentType(ext), detectType)) {
            log.error("이미지 타입이 확장자와 다릅니다. | ext: {}, detectType: {}", ext, detectType);
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    public String getContentType(String key) {
        switch (key) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            default:
                return "application/octet-stream"; // 기본값
        }
    }
}
