package com.example.demo.service.statistics;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.dto.statistics.CountDiariesRequest;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DiaryRepository diaryRepository;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    public ResponseEntity<Long> countDiaries(String accessToken, CountDiariesRequest request) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        LocalDateTime startDate = LocalDateTime.of(request.getYear(), request.getMonth(), 1, 0, 0, 0); // 해당 월의 첫 번째 날의 자정(00:00:00)
        LocalDateTime endDate = startDate.withDayOfMonth(startDate.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59); // 해당 월의 마지막 날의 23:59:59

        List<Diary> diaries = diaryRepository.findByAccountAndCreatedAtBetween(account, startDate, endDate);
        long count = diaries.size();

        return ResponseEntity.ok(count);

    }

}
