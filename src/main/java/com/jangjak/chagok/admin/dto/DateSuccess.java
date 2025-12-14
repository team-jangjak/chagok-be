package com.jangjak.chagok.admin.dto;

import java.time.LocalDate;

public record DateSuccess(
        LocalDate actionDate,
        boolean isSuccess
) {}
