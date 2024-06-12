package com.example.demo.domain.diary;

import com.example.demo.domain.account.Account;
import com.example.demo.dto.diary.request.CommentRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    public Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diary diary;

    public void updateComment(CommentRequest request) {
        this.content = request.getContent();
    }

}
