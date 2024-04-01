package com.example.demo.service.diary;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.Likes;
import com.example.demo.dto.diary.response.LikesResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.diary.DiaryRepository;
import com.example.demo.repository.diary.LikesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final DiaryRepository diaryRepository;
    private final AccountRepository accountRepository;
    private final LikesRepository likesRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<String> postLike(String accessToken, long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        if(likesRepository.existsByAccountAndDiary(account, diary)) {
            throw new RuntimeException("Already liked");
        } else {
            Likes likes = Likes.builder()
                    .account(account)
                    .diary(diary)
                    .build();

            likesRepository.save(likes);

            diary.setLikeCount(diary.getLikeCount() + 1);
            diaryRepository.save(diary);
        }

        return ResponseEntity.ok("Like successful");

    }

    @Transactional
    public ResponseEntity<LikesResponse> getLikes(long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        LikesResponse response = LikesResponse.builder()
                .msg("좋아요 수 : ")
                .likeCount(diary.getLikeCount())
                .build();

        return ResponseEntity.ok(response);

    }

    @Transactional
    public ResponseEntity<String> unLike(String accessToken, long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Likes likes = likesRepository.findByAccountAndDiary(account, diary).orElse(null);

        if(likes == null) {
            throw new RuntimeException("Not found Like");
        } else {
            likesRepository.delete(likes);

            diary.setLikeCount(diary.getLikeCount() - 1);
            diaryRepository.save(diary);
        }

        return ResponseEntity.ok("Unlike Successful");

    }

}
