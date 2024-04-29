package com.example.demo.dto.statistics;

import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.Emotion;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.LocalDate;

@Data
public class CountDiariesResponse {

    private long diaryId;

    private Emotion emotion;

    private int likeCount;

    private int viewCount;

    private LocalDate createdAt;

    private Point geography;

    public CountDiariesResponse(Diary diary) {
        this.diaryId = diary.getDiaryId();
        this.emotion = diary.getEmotion();
        this.likeCount = diary.getLikeCount();
        this.viewCount = diary.getViewCount();
        this.createdAt = diary.getCreatedAt().toLocalDate();
        this.geography = diary.getGeograpy();
    }

}