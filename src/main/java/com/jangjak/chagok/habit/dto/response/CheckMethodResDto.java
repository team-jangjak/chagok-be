package com.jangjak.chagok.habit.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckMethodResDto {
    Long id;
    String title;
    List<CheckMethodDetailRestDto> details;
}
