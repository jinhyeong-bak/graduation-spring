package com.example.demo.controller.diary;


import com.example.demo.dto.diary.request.DiaryCreationReq;
import com.example.demo.dto.diary.request.DiaryListRequest;
import com.example.demo.dto.diary.request.DiaryModificationReq;
import com.example.demo.dto.diary.response.DiaryListResponse;
import com.example.demo.dto.diary.response.DiaryReadResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.AwsS3Service;
import com.example.demo.service.diary.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DiaryController {

    private final JwtUtil jwtUtil;
    private final DiaryService diaryService;
    private final AwsS3Service awsS3Service;

    @PostMapping("/diary")
    public ResponseEntity<String> postDiary(HttpServletRequest request, @RequestBody Map<String, Object> diaryCreationReq,
                                            @RequestPart(value = "image", required = false) List<MultipartFile> diaryImages) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            ObjectMapper objectMapper = new ObjectMapper();
            DiaryCreationReq creationReq = objectMapper.convertValue(diaryCreationReq.get("diaryCreationReq"), DiaryCreationReq.class);

            List<String> imageUrls = new ArrayList<>();
            if (diaryImages != null) {
                diaryImages.forEach(file -> {
                    if (!file.isEmpty()) {
                        String imageUrl = null;
                        try {
                            imageUrl = awsS3Service.uploadImageToS3(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        imageUrls.add(imageUrl);
                    }
                });
            }


            return diaryService.createDiary(accessToken, creationReq, imageUrls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PatchMapping("/diary")
    public ResponseEntity<String> patchDiary(HttpServletRequest request, @RequestBody Map<String, Object> diaryModificationReq,
                                             @RequestPart(value = "image", required = false) List<MultipartFile> diaryImages) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            ObjectMapper objectMapper = new ObjectMapper();
            DiaryModificationReq modificationReq = objectMapper.convertValue(diaryModificationReq.get("modificationReq"), DiaryModificationReq.class);
            diaryService.deleteFile(modificationReq.getDiaryId());

            List<String> imageUrls = new ArrayList<>();
            if (diaryImages != null) {
                diaryImages.forEach(file -> {
                    String imageUrl = null;
                    try {
                        imageUrl = awsS3Service.uploadImageToS3(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    imageUrls.add(imageUrl);
                });
            }

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

    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<DiaryReadResponse> DiaryReadReq(HttpServletRequest request, @PathVariable(name="diaryId") long diaryId) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
             DiaryReadResponse diaryReadResponse = diaryService.readDiary(accessToken, diaryId);
             return ResponseEntity.ok().body(diaryReadResponse);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/diary/list")
    public ResponseEntity<DiaryListResponse> DiaryListReq(HttpServletRequest request, @RequestBody DiaryListRequest diaryListRequest) {
        try {
            String accessToken = jwtUtil.getAccessTokenFromHeader(request);
            DiaryListResponse diaryListResponse = diaryService.getDiaryList(accessToken, diaryListRequest);
            return ResponseEntity.ok().body(diaryListResponse);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}