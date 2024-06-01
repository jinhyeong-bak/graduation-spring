package com.example.demo.dto.diary.request;

import com.example.demo.domain.diary.Emotion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCreationReq {

    private String title;

    private String content;

    private Boolean commentEnabled;

    private Boolean isPublic;

    private Double longitude;

    private Double latitude;

    private Emotion emotion;

}
