package com.example.demo.repository.diary;

import com.example.demo.domain.account.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    boolean existsByAccountAndDiary(Account account, Diary diary);

    Optional<Likes> findByAccountAndDiary(Account account, Diary diary);

}
