package com.example.demo.controller.diary;

import com.example.demo.dto.diary.response.LikesResponse;
import com.example.demo.dto.diary.response.ViewResponse;
import com.example.demo.exception.api.ApiResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.diary.ViewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class ViewController {

    private final ViewService viewService;
    private final JwtUtil jwtUtil;

    @PostMapping("/view/{diaryId}")
    public ApiResponse<String> view(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(viewService.view(accessToken, diaryId));
    }

    @GetMapping("/view/{diaryId}")
    public ApiResponse<ViewResponse> getViews(@PathVariable(name = "diaryId") long diaryId) {
        return new ApiResponse<>(viewService.getViews(diaryId));
    }

}
