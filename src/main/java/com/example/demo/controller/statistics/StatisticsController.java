package com.example.demo.controller.statistics;

import com.example.demo.dto.statistics.CountDiariesRequest;
import com.example.demo.dto.statistics.StatisticsResponse;
import com.example.demo.exception.api.ApiResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.statistics.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService service;
    private final JwtUtil jwtUtil;

    @GetMapping("/diary-count")
    public ApiResponse<List<StatisticsResponse>> countDiaries(HttpServletRequest request, @RequestParam int year, @RequestParam int month) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(service.countDiaries(accessToken, year, month));
    }

    @GetMapping("/diary-most-like-count")
    public ApiResponse<StatisticsResponse> getMostLikedDiary(HttpServletRequest request) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(service.getMostLikedDiary(accessToken));
    }

    @GetMapping("/diary-most-view-count")
    public ApiResponse<StatisticsResponse> getMostViewdDiary(HttpServletRequest request) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(service.getMostViewedDiary(accessToken));
    }

}
