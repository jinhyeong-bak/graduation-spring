package com.example.demo.service.diary;

import com.example.demo.domain.account.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.View;
import com.example.demo.dto.diary.response.ViewResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.account.AccountRepository;
import com.example.demo.repository.diary.DiaryRepository;
import com.example.demo.repository.diary.ViewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final DiaryRepository diaryRepository;
    private final AccountRepository accountRepository;
    private final ViewRepository viewRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public String view(String accessToken, long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

       View view = View.builder()
               .account(account)
               .diary(diary)
               .build();

       viewRepository.save(view);

       diary.setViewCount(diary.getViewCount() + 1);
       diaryRepository.save(diary);

        return "View successful";

    }

    @Transactional
    public ViewResponse getViews(long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        ViewResponse response = ViewResponse.builder()
                .viewCount(diary.getViewCount())
                .build();

        return response;

    }

}
