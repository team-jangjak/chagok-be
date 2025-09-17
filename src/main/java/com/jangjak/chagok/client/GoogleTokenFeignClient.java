package com.jangjak.chagok.client;

import com.jangjak.chagok.user.dto.google.GoogleReqDto;
import com.jangjak.chagok.user.dto.google.GoogleResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

// 토큰 발급용 FeignClient
@FeignClient(name = "googleTokenClient", url = "${google.auth-url}")
public interface GoogleTokenFeignClient {
    @PostMapping("/token")
    ResponseEntity<GoogleResDto> getGoogleToken(GoogleReqDto googleTokenRequest);
}


