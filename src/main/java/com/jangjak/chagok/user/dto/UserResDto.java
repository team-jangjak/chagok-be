package com.jangjak.chagok.user.dto;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.user.enums.GENDER;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResDto {
    String name;
    String email;
    LocalDate birthDate;
    String profileImage;
    Integer tendency;
    GENDER gender;
    YN active;
    Long point;
}
