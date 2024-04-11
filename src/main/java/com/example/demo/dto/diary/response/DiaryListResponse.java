package com.example.demo.dto.diary.response;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.Emotion;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import java.util.LinkedList;
import java.util.List;

@Data
public class DiaryListResponse {

    private List<DiaryListEntry> diaryList = new LinkedList<>();
    private int curPage;
    private int totalPage;
    @Data
    private static class DiaryListEntry {
        private Double longitude;
        private Double latitude;
        private Emotion emotion;
        private boolean isCurrentUserDiary;
    }

    public static DiaryListResponse of(Page<Diary> diaryPage, long requestUserId) {
        DiaryListResponse diaryListResponse = new DiaryListResponse();

        List<Diary> diaries = diaryPage.getContent();

        for(Diary diary : diaries) {
            DiaryListEntry entry = new DiaryListEntry();

            // 요청 유저가 작성한 글인가
            Account account = diary.getAccount();
            if(account.getId() - requestUserId == 0) {
                entry.isCurrentUserDiary = true;
            }

            Point geograpy = diary.getGeograpy();
            entry.longitude = geograpy.getY();
            entry.latitude = geograpy.getY();

            entry.emotion = diary.getEmotion();

            diaryListResponse.getDiaryList().add(entry);
        }

        diaryListResponse.totalPage = diaryPage.getTotalPages();
        diaryListResponse.curPage = diaryPage.getNumber();

        return diaryListResponse;
    }
}


