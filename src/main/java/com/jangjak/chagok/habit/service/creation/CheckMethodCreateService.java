package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.habit.dto.request.create.CreateCheckMethodRequestDto;
import com.jangjak.chagok.habit.entity.CheckMethod;
import com.jangjak.chagok.habit.entity.CheckMethodDetail;
import com.jangjak.chagok.habit.repository.CheckMethodDetailRepository;
import com.jangjak.chagok.habit.repository.CheckMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckMethodCreateService {

    private final CheckMethodRepository checkMethodRepository;
    private final CheckMethodDetailRepository checkMethodDetailRepository;

    // new 템플릿 생성
    @Transactional
    public Long createCheckMethod(Long id, CreateCheckMethodRequestDto requestDto) {

        // userId 검증

        // checkMethod: userId, title
         CheckMethod checkMethod = CheckMethod.builder()
                .userId(id)
                .title(requestDto.getTitle())
                .build();

        CheckMethod savedCheckMethod = checkMethodRepository.save(checkMethod);

        // checkMethodDetail: checkMethodId, order, type, value
        List<CheckMethodDetail> detailEntities = requestDto.getDetails().stream()
                .map(detailDto -> CheckMethodDetail.builder()
                        .checkMethodId(savedCheckMethod.getId())
                        .methodOrder(detailDto.getMethodOrder())
                        .type(detailDto.getType())
                        .value(detailDto.getValue())
                        .build())
                .toList();

        checkMethodDetailRepository.saveAll(detailEntities);

        return savedCheckMethod.getId();
    }

}
