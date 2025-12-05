package com.jangjak.chagok.external.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.external.dto.request.ImageUploadRequestDto;
import com.jangjak.chagok.external.service.s3.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final S3ImageService imageService;

    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("meta") ImageUploadRequestDto meta
    ) {
        List<String> res = imageService.uploadImage(images, meta);
        return CommonResponse.toRes(res, "이미지 성공적으로 업로드되었습니다.");
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestPart("image") MultipartFile image,
            @RequestPart("meta") ImageUploadRequestDto meta
    ) {
        String res = imageService.uploadImage(image, meta);
        return CommonResponse.toRes(res, "이미지 성공적으로 업로드되었습니다.");
    }

    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfileImage(
            @RequestPart("image") MultipartFile image,
            @RequestPart("meta") ImageUploadRequestDto meta
    ) {
        String res = imageService.uploadProfileImage(image, meta);
        return CommonResponse.toRes(res, "이미지 성공적으로 업로드되었습니다.");
    }

}
