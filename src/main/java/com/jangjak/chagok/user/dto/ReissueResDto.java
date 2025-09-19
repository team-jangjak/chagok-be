package com.jangjak.chagok.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@Builder
@AllArgsConstructor
public class ReissueResDto {
    private final ResponseCookie accessCookie;
    private final ResponseCookie refreshCookie;
}
