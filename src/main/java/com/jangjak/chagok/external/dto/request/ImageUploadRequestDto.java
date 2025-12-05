package com.jangjak.chagok.external.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageUploadRequestDto {
    private Integer type;
    private Integer id; // userActionId or habitId
}
