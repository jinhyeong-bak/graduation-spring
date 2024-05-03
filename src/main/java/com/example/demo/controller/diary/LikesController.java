package com.example.demo.controller.diary;

import com.example.demo.dto.diary.response.LikesResponse;
import com.example.demo.exception.api.ApiResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.diary.LikesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class LikesController {

    private final JwtUtil jwtUtil;
    private final LikesService likesService;

    @PostMapping("/likes/{diaryId}")
    public ApiResponse<String> postLike(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(likesService.postLike(accessToken, diaryId));
    }

    @GetMapping("/likes/{diaryId}")
    public ApiResponse<LikesResponse> getLikes(@PathVariable(name = "diaryId") long diaryId) {
        return new ApiResponse<>(likesService.getLikes(diaryId));
    }

    @DeleteMapping("/likes/{diaryId}")
    public ApiResponse<String> deleteLike(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(likesService.unLike(accessToken, diaryId));
    }

}
