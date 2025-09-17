package com.jangjak.chagok.client;

import com.jangjak.chagok.user.dto.google.GoogleDetailResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// 사용자 정보 조회용 FeignClient
@FeignClient(name = "google", url = "${google.user-url}")
public interface GoogleFeignClient {
    @GetMapping("/userinfo")
    ResponseEntity<GoogleDetailResDto> getUserInfo(@RequestHeader("Authorization") String bearerToken);
}


