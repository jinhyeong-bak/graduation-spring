package com.example.demo.controller.statistics;

import com.example.demo.dto.statistics.CountDiariesRequest;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.statistics.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService service;
    private final JwtUtil jwtUtil;

    @PostMapping("/diary-count")
    public ResponseEntity<Long> postLike(HttpServletRequest request, @RequestBody CountDiariesRequest countRequest) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return service.countDiaries(accessToken, countRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
