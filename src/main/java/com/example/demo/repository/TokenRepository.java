package com.example.demo.repository;

import com.example.demo.infrastructure.jwt.JwtEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TokenRepository {

    private final EntityManager em;
    public Long save(JwtEntity jwtEntity) {
        em.persist(jwtEntity);
        return jwtEntity.getId();
    }

    public Optional<JwtEntity> findByUserPk(Long userPk) {
        List<JwtEntity> resultList = em.createQuery("select t from JwtEntity t where userPk = :pk", JwtEntity.class)
                .setParameter("pk", userPk)
                .getResultList();

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

}
