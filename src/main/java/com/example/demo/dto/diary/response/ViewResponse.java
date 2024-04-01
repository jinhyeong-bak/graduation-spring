package com.example.demo.dto.diary.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewResponse {

    private String msg;

    private int viewCount;


}
