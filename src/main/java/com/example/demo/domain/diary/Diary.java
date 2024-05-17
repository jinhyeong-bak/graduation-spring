package com.example.demo.domain.diary;

import com.example.demo.domain.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long diaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private String title;

    private String content;

    private int viewCount;

    private int likeCount;

    private Boolean commentEnabled;

    private Boolean isPublic;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Point geography;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;


    public void updateDiary(String title, String content, Boolean commentEnabled, Emotion emotion) {
        this.title = title;
        this.content = content;
        this.commentEnabled = commentEnabled;
        this.emotion = emotion;
    }

}
