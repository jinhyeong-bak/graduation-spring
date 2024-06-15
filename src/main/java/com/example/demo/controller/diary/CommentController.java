package com.example.demo.controller.diary;

import com.example.demo.dto.diary.request.CommentRequest;
import com.example.demo.dto.diary.response.CommentResponse;
import com.example.demo.exception.api.ApiResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.diary.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class CommentController {

    private final CommentService commentService;

    private final JwtUtil jwtUtil;

    @PostMapping("/comment/{diaryId}")
    public ApiResponse<String> postLike(HttpServletRequest request, @RequestBody CommentRequest commentRequest,
                                        @PathVariable(name = "diaryId") long diaryId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(commentService.createComment(accessToken, commentRequest, diaryId));

    }

    @PatchMapping("/comment/{commentId}")
    public ApiResponse<String> updateComment(HttpServletRequest request, @RequestBody CommentRequest commentRequest,
                                                @PathVariable(name = "commentId") long commentId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(commentService.modifyComment(accessToken, commentRequest, commentId));
    }

    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<String> deleteComment(HttpServletRequest request, @PathVariable(name = "commentId") long commentId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(commentService.deleteComment(accessToken, commentId));
    }

    @GetMapping("/comment/{diaryId}")
    public ApiResponse<List<CommentResponse>> getComments(HttpServletRequest request,
                                                          @PathVariable(name = "diaryId") long diaryId) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        return new ApiResponse<>(commentService.getComments(accessToken, diaryId));
    }

}
