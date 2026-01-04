package com.jangjak.chagok.habit.dto.request.update;

import com.jangjak.chagok.habit.dto.request.create.CheckMethodDetailRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class CheckMethodUpdateRequestDto {
    String title;
    List<CheckMethodDetailRequestDto> details;
}
