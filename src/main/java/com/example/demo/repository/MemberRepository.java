package com.example.demo.repository;

import com.example.demo.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MemberRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(Member member) {
        log.info("save 함수 호출 email={}", member.getEmail());
        em.persist(member);
        return member.getId();
    }

    public Optional<Member> findByEmail(String email) {
        log.info("findByEmail 호출 email={}", email);

        String query = "select m from Member m where m.email = :email";

        List<Member> result = em.createQuery(query, Member.class)
                .setParameter("email", email)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
