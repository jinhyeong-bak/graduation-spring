package com.example.demo.dto.statistics;

import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.Emotion;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StatisticsResponse {

    private long diaryId;

    private Emotion emotion;

    private int likeCount;

    private int viewCount;

    private LocalDate createdAt;

    private Double latitude;

    private Double longitude;

    public StatisticsResponse(Diary diary) {
        this.diaryId = diary.getDiaryId();
        this.emotion = diary.getEmotion();
        this.likeCount = diary.getLikeCount();
        this.viewCount = diary.getViewCount();
        this.createdAt = diary.getCreatedAt().toLocalDate();
        this.longitude = diary.getGeography().getX();
        this.latitude = diary.getGeography().getY();
    }

}