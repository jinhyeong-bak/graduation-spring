package com.example.demo.dto.diary.response;


import com.example.demo.domain.account.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.DiaryImage;
import com.example.demo.domain.diary.Emotion;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    private Double longitude;

    private Double latitude;

    private Emotion emotion;

    @Builder.Default
    private List<String> imgUrls = new LinkedList<>();

    private boolean requestUserDiary;
    private boolean requestUserLiked;
    private boolean isPublic;

    public static DiaryReadResponse toDto(Diary diary, long requestUserId) {

        // 요청 유저가 작성한 글인가
        boolean isRequestUserDiary = false;
        Account account = diary.getAccount();
        if(account.getId() - requestUserId == 0) {
            isRequestUserDiary = true;
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
                .requestUserDiary(isRequestUserDiary)
                .isPublic(diary.getIsPublic())
                .build();
    }

    // 이미지 추가 api
    public void addImgUrls(List<DiaryImage> images) {
        for(DiaryImage diaryImage : images) {
            imgUrls.add(diaryImage.getDiaryImage());
        }
    }

    // 좋아요 조회 api
    public void requestUserLikedThisPost(boolean result) {
        requestUserLiked = result;
    }
}
