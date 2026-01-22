package com.jangjak.chagok.point.service;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.user.entity.User;
import com.jangjak.chagok.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointService {
    private final UserRepository userRepository;

    // 포인트 적립
    @Transactional
    public Long earnPoints(Long userId, Long point) {
        User user = userRepository.findByUserIdAndActive(userId, YN.Y).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));
        user.savePoints(user.getPoint() + point);

        return user.getPoint();
    }
}
