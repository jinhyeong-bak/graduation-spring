package com.example.demo.dto.diary.response;

import com.example.demo.domain.diary.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {

    private long commentId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
