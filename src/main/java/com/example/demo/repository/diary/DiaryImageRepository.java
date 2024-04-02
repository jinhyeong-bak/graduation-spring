package com.example.demo.repository.diary;

import com.example.demo.domain.diary.DiaryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryImageRepository extends JpaRepository<DiaryImage, Long> {

    void deleteDiaryImageByDiaryDiaryId(long diaryId);

    List<DiaryImage> findByDiaryDiaryId(long diaryId);

}
