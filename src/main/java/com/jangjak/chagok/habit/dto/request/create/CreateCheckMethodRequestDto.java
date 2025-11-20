package com.jangjak.chagok.habit.dto.request.create;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateCheckMethodRequestDto {
    String title;
    List<CheckMethodDetailRequestDto> details;
}
