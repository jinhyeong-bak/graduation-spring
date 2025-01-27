package com.example.demo.service.diary;

import com.example.demo.domain.account.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.DiaryImage;
import com.example.demo.dto.diary.request.DiaryCreationReq;
import com.example.demo.dto.diary.request.DiaryListRequest;
import com.example.demo.dto.diary.request.DiaryModificationReq;
import com.example.demo.dto.diary.response.DiaryListResponse;
import com.example.demo.dto.diary.response.DiaryReadResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.account.AccountRepository;
import com.example.demo.repository.diary.DiaryImageRepository;
import com.example.demo.repository.diary.DiaryRepository;
import com.example.demo.repository.diary.LikesRepository;
import com.example.demo.service.AwsS3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final AccountRepository accountRepository;
    private final DiaryRepository diaryRepository;
    private final LikesRepository likesRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final AwsS3Service awsS3Service;
    private final JwtUtil jwtUtil;
    private final int pageSize = 30;

    @Transactional
    public String createDiary(String accessToken, DiaryCreationReq request, List<String> diaryImages) {

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));


        Point location = setLocation(request.getLongitude(), request.getLatitude());
        //Point2D.Double location = setLocation(request.getLatitude(), request.getLongitude());

        Diary diary = Diary.builder()
                .account(account)
                .title(request.getTitle())
                .content(request.getContent())
                .commentEnabled(request.getCommentEnabled())
                .geography(location)
                .isPublic(request.getIsPublic())
                .emotion(request.getEmotion())
                .createdAt(LocalDateTime.now())
                .build();

        diaryRepository.save(diary);

        for(String imageUrl : diaryImages) {
            DiaryImage diaryImage = DiaryImage.builder()
                    .diary(diary)
                    .diaryImage(imageUrl)
                    .build();
            diaryImageRepository.save(diaryImage);
        }

        return "Creation Success";

    }

    @Transactional
    public String modifyDiary(String accessToken, DiaryModificationReq request, List<String> diaryImages) {

        Diary diary = diaryRepository.findById(request.getDiaryId())
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        if(account.getId() != diary.getAccount().getId()) {
            throw new RuntimeException("You can only modify diary authored by yourself.");
        } else {
            diary.setUpdatedAt(LocalDateTime.now());
            if(request.getTitle() != null && !request.getTitle().isEmpty()) {
                diary.setTitle(request.getTitle());
            }
            if(request.getContent() != null && !request.getContent().isEmpty()) {
                diary.setContent(request.getContent());
            }
            if (request.getCommentEnabled() != null) {
                diary.setCommentEnabled(request.getCommentEnabled());
            }
            if(request.getIsPublic() != null) {
                diary.setIsPublic(request.getIsPublic());
            }
            if(request.getEmotion() != null) {
                diary.setEmotion(request.getEmotion());
            }
            diaryRepository.save(diary);

            for(String imageUrl : diaryImages) {
                DiaryImage diaryImage = DiaryImage.builder()
                        .diary(diary)
                        .diaryImage(imageUrl)
                        .build();
                diaryImageRepository.save(diaryImage);
            }
        }

        return "Modification Success";

    }

    @Transactional
    public String deleteDiary(String accessToken, long diaryId) {

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        long userId = jwtUtil.getUserPk(accessToken);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found User"));

        if(account.getId() != diary.getAccount().getId()) {
            throw new RuntimeException("You can only delete diary authored by yourself.");
        } else {
            List<DiaryImage> diaryImages = diaryImageRepository.findByDiaryDiaryId(diaryId);
            for (DiaryImage diaryImage : diaryImages) {
                awsS3Service.deleteFile(diaryImage.getDiaryImage());
            }

            diaryRepository.deleteById(diaryId);
            deleteFile(diaryId);
        }

        return "Deletion Success";

    }

    @Transactional
    public DiaryReadResponse readDiary(String accessToken, long diaryId) {

        long requestUserId = jwtUtil.getUserPk(accessToken);

        // 다이어리 가져오기
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Not found Diary"));

        DiaryReadResponse response = DiaryReadResponse.toDto(diary, requestUserId);

        // 이미지 가져오기
        List<DiaryImage> diaryImages = diaryImageRepository.findByDiaryDiaryId(diaryId);
        response.addImgUrls(diaryImages);

        // 요청 유저가 이 게시글에 좋아요를 눌렀는가
        Account requestUser = accountRepository.findById(requestUserId).orElseThrow(
                () -> new RuntimeException("Not found requestUser")
        );
        boolean isPostLikedByRequestUser = likesRepository.existsByAccountAndDiary(requestUser, diary);
        response.requestUserLikedThisPost(isPostLikedByRequestUser);

        return response;
    }

    @Transactional
    public DiaryListResponse getDiaryList(String accessToken, DiaryListRequest diaryListRequest)  {
        long requestUserPk = jwtUtil.getUserPk(accessToken);

        Geometry boundary = createBoundary(diaryListRequest.getLongitudeTopLeft(), diaryListRequest.getLatitudeTopLeft(),
                diaryListRequest.getLongitudeBottomRight(), diaryListRequest.getLatitudeBottomRight());

        Pageable pageable = PageRequest.of(diaryListRequest.getReqPage(), pageSize);
        Page<Diary> diaryPage = null;

        switch (diaryListRequest.getOption()) {
            case LIKE:
                diaryPage = diaryRepository.getDiaryListOrderByLike(boundary, requestUserPk, pageable);
                break;
            case VIEW:
                diaryPage = diaryRepository.getDiaryListOrderByView(boundary, requestUserPk, pageable);
                break;
            case RECENT:
                diaryPage = diaryRepository.getDiaryListOrderByCreatedAt(boundary, requestUserPk, pageable);
                break;
        }

        return DiaryListResponse.of(diaryPage, requestUserPk);
    }



    public Point setLocation(double longitude, double latitude)  {
        try {
            String pointWKT = "Point (" + longitude + " " + latitude + ")";

            Geometry now = new WKTReader().read(pointWKT);

            return (Point) now;
        } catch (Exception e) {
            throw new RuntimeException("Throw ParseException in setLocation method");
        }
    }

    public Geometry createBoundary(double lonTopLeft, double latTopLeft, double lonBottomRight, double latBottomRight) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry boundary = geometryFactory.createPolygon(new Coordinate[] {
                new Coordinate(lonTopLeft, latTopLeft),
                new Coordinate(lonBottomRight, latTopLeft),
                new Coordinate(lonBottomRight, latBottomRight),
                new Coordinate(lonTopLeft, latBottomRight),
                new Coordinate(lonTopLeft, latTopLeft)
        });

        return boundary;
    }



    public void deleteFile(long diaryId) {

        diaryImageRepository.deleteDiaryImageByDiaryDiaryId(diaryId);

    }

}
