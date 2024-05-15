package com.example.demo.dto.diary.response;


import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.DiaryImage;
import com.example.demo.domain.diary.Emotion;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
public class DiaryReadResponse {

    private long diaryId;

    private String title;

    private String content;

    private int viewCount;

    private int likeCount;

    private Boolean commentEnabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Double longitude;

    private Double latitude;

    private Emotion emotion;

    private List<String> imgUrls = new LinkedList<>();

    private boolean isCurrentUserDiary;

    public static DiaryReadResponse toDto(Diary diary, long requestUserId) {

        // 요청 유저가 작성한 글인가
        boolean isCurrentUserDiary = false;
        Account account = diary.getAccount();
        if(account.getId() - requestUserId == 0) {
            isCurrentUserDiary = true;
        }

        return DiaryReadResponse.builder()
                .diaryId(diary.getDiaryId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .viewCount(diary.getViewCount())
                .likeCount(diary.getLikeCount())
                .commentEnabled(diary.getCommentEnabled())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .longitude(diary.getGeography().getX())
                .latitude(diary.getGeography().getY())
                .emotion(diary.getEmotion())
                .isCurrentUserDiary(isCurrentUserDiary)
                .build();
    }

    // 이미지 추가 api
    public void addImgUrls(List<DiaryImage> images) {
        for(DiaryImage diaryImage : images) {
            imgUrls.add(diaryImage.getDiaryImage());
        }
    }
}
