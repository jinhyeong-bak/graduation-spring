package com.example.demo.dto.diary.request;

import lombok.Data;

@Data
public class DiaryListRequest {
    private Double longitudeTopLeft;
    private Double latitudeTopLeft;
    private Double longitudeBottomRight;
    private Double latitudeBottomRight;
    private DiaryListOption option;
    private int reqPage;
}

