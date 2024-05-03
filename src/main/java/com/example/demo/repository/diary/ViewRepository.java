package com.example.demo.repository.diary;

import com.example.demo.domain.Account;
import com.example.demo.domain.diary.Diary;
import com.example.demo.domain.diary.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {

}
