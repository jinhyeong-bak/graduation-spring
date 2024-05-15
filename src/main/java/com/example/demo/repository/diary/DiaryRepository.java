package com.example.demo.repository.diary;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByAccountAndCreatedAtBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select t from Diary as t where within(t.geography, :boundary) = true order by t.likeCount desc ")
    Page<Diary> getDiaryListOrderByLike(@Param("boundary") Geometry boundary, Pageable pageable);

    @Query(value = "select t from Diary as t where within(t.geography, :boundary) = true order by t.viewCount desc")
    Page<Diary> getDiaryListOrderByView(@Param("boundary") Geometry boundary, Pageable pageable);

    @Query(value = "select t from Diary as t where within(t.geography, :boundary) = true order by t.createdAt desc")
    Page<Diary> getDiaryListOrderByCreatedAt(@Param("boundary") Geometry boundary, Pageable pageable);

    List<Diary> findAllByAccountOrderByLikeCountDesc(Account account);

    List<Diary> findAllByAccountOrderByViewCountDesc(Account account);

}

