package com.jangjak.chagok.user.service;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.user.dto.UserReqDto;
import com.jangjak.chagok.user.entity.User;
import com.jangjak.chagok.user.repository.OauthRepository;
import com.jangjak.chagok.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;

    public Long userCreate(@Valid UserReqDto userReqDto) {
        userRepository.findByOauthId(userReqDto.getOauthId()).ifPresent(user -> {
            throw new CustomException("이미 가입된 사용자입니다.", HttpStatus.BAD_REQUEST);
        });

        if(oauthRepository.findById(userReqDto.getOauthId()).isEmpty()){
            throw new CustomException("존재하지 않는 oauth", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .oauthId(userReqDto.getOauthId())
                .name(userReqDto.getName())
                .email(userReqDto.getEmail())
                .profileImage(userReqDto.getProfileImage())
                .birthDate(userReqDto.getBirthDate())
                .tendency(userReqDto.getTendency())
                .build();

        userRepository.save(user);
        return user.getId();

    }

}
