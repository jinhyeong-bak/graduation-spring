package com.example.demo.controller.diary;


import com.example.demo.dto.diary.request.DiaryCreationReq;
import com.example.demo.dto.diary.request.DiaryModificationReq;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.AwsS3Service;
import com.example.demo.service.diary.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController {

    private final JwtUtil jwtUtil;
    private final DiaryService diaryService;
    private final AwsS3Service awsS3Service;

    @PostMapping("/diary")
    public ResponseEntity<String> postDiary(HttpServletRequest request, @RequestBody DiaryCreationReq diaryCreationReq,
                                            @RequestPart(value = "image", required = false) List<MultipartFile> diaryImages) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            List<String> imageUrls = new ArrayList<>();
            diaryImages.forEach(file -> {
                if(!file.isEmpty()) {
                    String imageUrl = null;
                    try {
                        imageUrl = awsS3Service.uploadImageToS3(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    imageUrls.add(imageUrl);
                }
            });

            return diaryService.createDiary(accessToken, diaryCreationReq, imageUrls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping("/diary")
    public ResponseEntity<String> patchDiary(HttpServletRequest request, @RequestBody DiaryModificationReq modificationReq,
                                             @RequestPart(value = "image", required = false) List<MultipartFile> diaryImages) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            diaryService.deleteFile(modificationReq.getDiaryId());

            List<String> imageUrls = new ArrayList<>();
            diaryImages.forEach(file -> {
                String imageUrl = null;
                try {
                    imageUrl = awsS3Service.uploadImageToS3(file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                imageUrls.add(imageUrl);
            });

            return diaryService.modifyDiary(accessToken, modificationReq, imageUrls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/diary/{diaryId}")
    public ResponseEntity<String> deleteDiary(HttpServletRequest request, @PathVariable(name = "diaryId") long diaryId){
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            return diaryService.deleteDiary(accessToken, diaryId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
