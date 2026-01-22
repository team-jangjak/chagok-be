package com.jangjak.chagok.point.controller;

import com.jangjak.chagok.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
@Slf4j
public class PointController {

    private final PointService pointService;
}
