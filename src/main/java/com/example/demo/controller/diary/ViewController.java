package com.example.demo.controller.diary;

import com.example.demo.dto.diary.response.LikesResponse;
import com.example.demo.dto.diary.response.ViewResponse;
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
    public ResponseEntity<String> view(@PathVariable(name = "diaryId") long diaryId) {
        try {
            return viewService.view(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/view/{diaryId}")
    public ResponseEntity<ViewResponse> getViews(@PathVariable(name = "diaryId") long diaryId) {
        try {
            return viewService.getViews(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
