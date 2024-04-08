package com.example.demo.controller.diary;

import com.example.demo.dto.diary.request.CommentRequest;
import com.example.demo.dto.diary.response.CommentResponse;
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
    public ResponseEntity<String> postLike(HttpServletRequest request, @RequestBody CommentRequest commentRequest,
                                           @PathVariable(name = "diaryId") long diaryId) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return commentService.createComment(accessToken, commentRequest, diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/comment/{commentId}")
    public ResponseEntity<String> updateComment(HttpServletRequest request, @RequestBody CommentRequest commentRequest,
                                                @PathVariable(name = "commentId") long commentId) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return commentService.modifyComment(accessToken, commentRequest, commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<String> deleteComment(HttpServletRequest request, @PathVariable(name = "commentId") long commentId) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return commentService.deleteComment(accessToken, commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comment/{diaryId}")
    public ResponseEntity<List<CommentResponse>> getComments(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId) {
        try {
            return commentService.getComments(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
