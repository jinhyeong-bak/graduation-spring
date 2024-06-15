package com.example.demo.init;

import com.example.demo.config.SecurityConfig;
import com.example.demo.domain.account.Account;
import com.example.demo.domain.diary.Comment;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.Emotion;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.repository.account.AccountRepository;
import com.example.demo.repository.diary.CommentRepository;
import com.example.demo.repository.diary.DiaryRepository;
import com.example.demo.service.diary.DiaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final DiaryRepository diaryRepository;
    private final CommentRepository commentRepository;
    private final SecurityConfig securityConfig;
    private final DiaryService diaryService;

    @Override
    public void run(String... args) throws Exception {
        initialize();

    }

    public void initialize() {
//        initAccounts();
//        initDiaries();
//        initComments();
    }

    public void initAccounts() {

        if (!accountRepository.existsByEmail("test1@naver.com")) {
            Account account1 = Account.createSignUpMember("test1", "test1@naver.com", securityConfig.passwordEncoder().encode("Password123!"), OAuthProvider.SELF);
            accountRepository.save(account1);
        }

        if (!accountRepository.existsByEmail("test2@naver.com")) {
            Account account2 = Account.createSignUpMember("test2", "test2@naver.com", securityConfig.passwordEncoder().encode("Password123!"), OAuthProvider.SELF);
            accountRepository.save(account2);
        }

        if (!accountRepository.existsByEmail("test3@naver.com")) {
            Account account3 = Account.createSignUpMember("test3", "test3@naver.com", securityConfig.passwordEncoder().encode("Password123!"), OAuthProvider.SELF);
            accountRepository.save(account3);
        }

        if (!accountRepository.existsByEmail("test4@naver.com")) {
            Account account4 = Account.createSignUpMember("test4", "test4@naver.com", securityConfig.passwordEncoder().encode("Password123!"), OAuthProvider.KAKAO);
            accountRepository.save(account4);
        }

        if (!accountRepository.existsByEmail("test5@naver.com")) {
            Account account5 = Account.createSignUpMember("test5", "test5@naver.com", securityConfig.passwordEncoder().encode("Password123!"), OAuthProvider.KAKAO);
            accountRepository.save(account5);
        }


        if (!accountRepository.existsByEmail("test@test.com")) {
            Account account = Account.createSignUpMember("test", "test@test.com", securityConfig.passwordEncoder().encode("Password123!"), OAuthProvider.SELF);
            accountRepository.save(account);
        }

    }

    public void initDiaries() {

        //가톨릭대학교 니콜스관
        createDiary("1", "첫 번째 일기", Emotion.JOY, 10, 0, true, true, 1, 126.802311, 37.485985);
        //가톨릭대학교 마리아관
        createDiary("2", "두 번째 일기", Emotion.JOY, 20, 5, true, true, 1, 126.802875, 37.486328);
        //가톨릭대학교 중앙도서관
        createDiary("3", "세 번째 일기", Emotion.SADNESS, 50, 30, true, true, 1, 126.799911, 37.486836);
        //가톨릭대학교 운동장
        createDiary("4", "네 번째 일기", Emotion.ANGER, 20, 17, true, true, 1, 126.800938, 37.487825);
        //가톨릭대학교 콘서트홀
        createDiary("5", "다섯 번째 일기", Emotion.DISGUST, 0, 0, false, false, 1, 126.799524, 37.488039);
        //역곡 크라이 치즈버거 인근
        createDiary("1", "첫 번째 일기", Emotion.ANXIETY, 10, 0, true, true, 2, 126.806021, 37.485333);
        //역곡 고등학교
        createDiary("2", "두 번째 일기", Emotion.ENVY, 20, 5, true, true, 2, 126.809622, 37.491979);
        //역곡역
        createDiary("1", "첫 번째 일기", Emotion.SADNESS, 50, 30, true, true, 3, 126.812014, 37.485298);
        //시청역
        createDiary("2", "두 번째 일기", Emotion.ENNUI, 0, 0, false , false, 3, 126.977158, 37.565490);
        //을지로입구역
        createDiary("1", "첫 번째 일기", Emotion.JOY, 10, 0, true, true, 4, 126.982161, 37.565923);
        //롯데백화점 본점
        createDiary("1", "첫 번째 일기", Emotion.ANGER, 10, 0, true, true, 5, 126.981754, 37.564698);
        //서울역
        createDiary("2", "두 번째 일기", Emotion.FEAR, 20, 5, true, true, 5, 126.970530, 37.554478);
        //용산역
        createDiary("3", "세 번째 일기", Emotion.SADNESS, 50, 30, true, true, 5, 126.964385, 37.529717);
        //이대역
        createDiary("4", "네 번째 일기", Emotion.ANGER, 40, 0, false, true, 5, 126.946852, 37.556849);
        //가톨릭대학교 성신교정
        createDiary("5", "다섯 번째 일기", Emotion.DISGUST, 0, 0, false, false, 5, 127.004596, 37.586310);
        //남경
        createDiary("남경 꿀맛!", "특제 탕수육 또 먹어야지~", Emotion.JOY, 48, 15, true, false, 6, 126.805261, 37.486181);
        // 역곡 공원
        createDiary("배드민턴 꿀잼!", "역시 운동은 즐거워", Emotion.JOY, 5, 3, true, false, 6, 126.805261, 37.486181);
        // 역곡 공원
        createDiary("풋살 꿀잼!", "담에 같이 하실 분~", Emotion.JOY, 9, 5, true, false, 6, 126.802454, 37.487865);
        // 스머프 동산
        createDiary("잊지 못할 추억!", "우리 우정 영원하자~!", Emotion.JOY, 4, 4, true, false, 6, 126.802856, 37.486620);
    }

    public void initComments() {

        createComment("힘내요", 1, 6);
        createComment("부러워요", 1, 7);
        createComment("즐거워요", 1, 10);
        createComment("무서워요", 1, 12);
        createComment("기뻐요", 2, 1);
        createComment("좋아요", 2, 2);
        createComment("화나요", 2, 4);
        createComment("슬퍼요", 2, 8);
        createComment("화나요", 2, 11);
        createComment("좋아요", 3, 2);
        createComment("파이팅", 3, 6);
        createComment("기뻐요", 3, 10);
        createComment("슬퍼요", 3, 13);
        createComment("즐거워요", 4, 1);
        createComment("할 수 있어요", 4, 6);
        createComment("부러워요", 5, 1);
        createComment("즐거워요", 5, 2);
        createComment("슬퍼요", 5, 3);
        createComment("힘내요", 5, 4);
        createComment("힘내세요", 5, 6);
        createComment("파이팅", 5, 7);
        createComment("슬퍼요", 5, 8);
        createComment("좋아요", 5, 10);

    }

    private void createDiary(String title, String content, Emotion emotion, int viewCount, int likeCount, Boolean commentEnabled, Boolean isPublic, long accountId, double longitude, double latitude) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Diary diary = new Diary();
        diary.setTitle(title);
        diary.setContent(content);
        diary.setEmotion(emotion);
        diary.setViewCount(viewCount);
        diary.setLikeCount(likeCount);
        diary.setCommentEnabled(commentEnabled);
        diary.setIsPublic(isPublic);
        diary.setAccount(account);
        diary.setGeography(diaryService.setLocation(longitude, latitude));
        diary.setCreatedAt(LocalDateTime.now());

        diaryRepository.save(diary);

    }

    private void createComment(String content, long accountId, long diaryId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary "));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAccount(account);
        comment.setDiary(diary);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

    }

}
