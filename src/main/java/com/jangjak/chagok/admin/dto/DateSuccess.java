package com.jangjak.chagok.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DateSuccess {
    LocalDate actionDate;
    boolean isSuccess;
}
