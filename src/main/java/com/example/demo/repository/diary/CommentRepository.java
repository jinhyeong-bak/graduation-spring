package com.example.demo.repository.diary;

import com.example.demo.domain.diary.Comment;;
import com.example.demo.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByDiary(Diary diary);
}
