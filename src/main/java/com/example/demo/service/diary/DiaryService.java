package com.example.demo.service.diary;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.DiaryImage;
import com.example.demo.dto.diary.request.DiaryCreationReq;
import com.example.demo.dto.diary.request.DiaryModificationReq;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.diary.DiaryImageRepository;
import com.example.demo.repository.diary.DiaryRepository;
import com.example.demo.service.AwsS3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final AccountRepository accountRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final AwsS3Service awsS3Service;
    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<String> createDiary(String accessToken, DiaryCreationReq request, List<String> diaryImages) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Point2D.Double location = setLocation(request.getLatitude(), request.getLongitude());

        Diary diary = Diary.builder()
                .account(account)
                .title(request.getTitle())
                .content(request.getContent())
                .commentEnabled(request.getCommentEnabled())
                .isPublic(request.getIsPublic())
                .geography(location)
                .emotion(request.getEmotion())
                .createdAt(LocalDateTime.now())
                .build();

        diaryRepository.save(diary);

        for(String imageUrl : diaryImages) {
            DiaryImage diaryImage = DiaryImage.builder()
                    .diary(diary)
                    .diaryImage(imageUrl)
                    .build();
            diaryImageRepository.save(diaryImage);
        }

        return ResponseEntity.ok("Creation Success");

    }

    @Transactional
    public ResponseEntity<String> modifyDiary(String accessToken, DiaryModificationReq request, List<String> diaryImages) {

        Diary diary = diaryRepository.findById(request.getDiaryId())
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        if(account.getId() != diary.getAccount().getId()) {
            throw new RuntimeException("You can only modify diary authored by yourself.");
        } else {
            diary.setUpdatedAt(LocalDateTime.now());
            if(request.getTitle() != null && !request.getTitle().isEmpty()) {
                diary.setTitle(request.getTitle());
            }
            if(request.getContent() != null && !request.getContent().isEmpty()) {
                diary.setContent(request.getContent());
            }
            if (request.getCommentEnabled() != null) {
                diary.setCommentEnabled(request.getCommentEnabled());
            }
            if(request.getIsPublic() != null) {
                diary.setIsPublic(request.getIsPublic());
            }
            if(request.getEmotion() != null) {
                diary.setEmotion(request.getEmotion());
            }
            diaryRepository.save(diary);

            for(String imageUrl : diaryImages) {
                DiaryImage diaryImage = DiaryImage.builder()
                        .diary(diary)
                        .diaryImage(imageUrl)
                        .build();
                diaryImageRepository.save(diaryImage);
            }
        }

        return ResponseEntity.ok("Modification Success");

    }

    @Transactional
    public ResponseEntity<String> deleteDiary(String accessToken, long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        if(account.getId() != diary.getAccount().getId()) {
            throw new RuntimeException("You can only delete diary authored by yourself.");
        } else {
            List<DiaryImage> diaryImages = diaryImageRepository.findByDiaryDiaryId(diaryId);
            for (DiaryImage diaryImage : diaryImages) {
                awsS3Service.deleteFile(diaryImage.getDiaryImage());
            }

            diaryRepository.deleteById(diaryId);
            deleteFile(diaryId);
        }

        return ResponseEntity.ok("Deletion Success");

    }

    public Point2D.Double setLocation(double latitude, double longitude){

        Point2D.Double now=new Point2D.Double();
        now.setLocation(latitude,longitude);

        return now;

    }

    public void deleteFile(long diaryId) {

        diaryImageRepository.deleteDiaryImageByDiaryDiaryId(diaryId);

    }

}
