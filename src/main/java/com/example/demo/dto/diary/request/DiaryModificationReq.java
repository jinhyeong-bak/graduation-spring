package com.example.demo.dto.diary.request;

import com.example.demo.domain.diary.Emotion;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class DiaryModificationReq {

    private long diaryId;

    private String title;

    private String content;

    private Boolean commentEnabled;

    private Point geography;

    private Emotion emotion;

}
