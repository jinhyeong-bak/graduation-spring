package com.example.demo.repository.diary;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByAccountAndCreatedAtBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);

}
