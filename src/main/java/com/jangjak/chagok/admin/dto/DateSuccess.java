package com.jangjak.chagok.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class DateSuccess {
    LocalDate actionDate;
    boolean isSuccess;
}
