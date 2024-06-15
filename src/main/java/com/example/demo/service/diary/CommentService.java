package com.example.demo.service.diary;

import com.example.demo.domain.account.Account;
import com.example.demo.domain.diary.Comment;
import com.example.demo.domain.diary.Diary;
import com.example.demo.dto.diary.request.CommentRequest;
import com.example.demo.dto.diary.response.CommentResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.account.AccountRepository;
import com.example.demo.repository.diary.CommentRepository;
import com.example.demo.repository.diary.DiaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final JwtUtil jwtUtil;

    private final AccountRepository accountRepository;

    private final DiaryRepository diaryRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public String createComment(String accessToken, CommentRequest request, long diaryId) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .account(account)
                .diary(diary)
                .build();

        commentRepository.save(comment);

        return "Success create Comment";

    }

    @Transactional
    public String modifyComment(String accessToken, CommentRequest request, long commentId) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Not found Comment"));

        if (account.getId() != comment.getAccount().getId()) {
            throw new RuntimeException("You can only modify comment authored by yourself.");
        } else {
            comment.setUpdatedAt(LocalDateTime.now());
            comment.updateComment(request);
        }
        commentRepository.save(comment);

        return "Success modify Comment";
    }

    @Transactional
    public String deleteComment(String accessToken, long commentId) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Not found Comment"));

        if(account.getId() != comment.getAccount().getId()) {
            throw new RuntimeException("You can only delete diary authored by yourself.");
        } else {
            commentRepository.deleteById(commentId);
        }

        return "Success delete Comment";

    }

    public List<CommentResponse> getComments(String accessToken, long diaryId) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        List<Comment> comments = commentRepository.findAllByDiary(diary);

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> {
                    boolean isOwner = comment.getAccount().getId() == userId;
                    return new CommentResponse(
                            comment.getCommentId(),
                            comment.getContent(),
                            comment.getCreatedAt(),
                            comment.getUpdatedAt(),
                            isOwner
                    );
                })
                .collect(Collectors.toList());

        return commentResponses;
    }

}
