package com.example.demo.dto.diary.request;

import com.example.demo.domain.diary.Emotion;
import lombok.Data;

@Data
public class DiaryCreationReq {

    private String title;

    private String content;

    private Boolean commentEnabled;

    private Double longitude;

    private Double latitude;

    private Emotion emotion;

}
