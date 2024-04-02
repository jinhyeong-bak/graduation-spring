package com.example.demo.controller.diary;

import com.example.demo.dto.diary.response.LikesResponse;
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
    public ResponseEntity<String> postLike(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return likesService.postLike(accessToken, diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/likes/{diaryId}")
    public ResponseEntity<LikesResponse> getLikes(@PathVariable(name = "diaryId") long diaryId) {
        try {
            return likesService.getLikes(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/likes/{diaryId}")
    public ResponseEntity<String> deleteLike(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return likesService.unLike(accessToken, diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
