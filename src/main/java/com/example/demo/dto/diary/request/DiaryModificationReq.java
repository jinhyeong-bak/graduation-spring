package com.example.demo.dto.diary.request;

import com.example.demo.domain.diary.Emotion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryModificationReq {

    private long diaryId;

    private String title;

    private String content;

    private Boolean commentEnabled;

    private Boolean isPublic;

    private Emotion emotion;

}
