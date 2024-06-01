package com.example.demo.repository.account;

import com.example.demo.domain.account.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class AccountRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(Account account) {
        log.info("save 함수 호출 email={}", account.getEmail());
        em.persist(account);
        return account.getId();
    }



    public Optional<Account> findByEmail(String email) {
        log.info("findByEmail 호출 email={}", email);

        String query = "select m from Account m where m.email = :email";

        List<Account> result = em.createQuery(query, Account.class)
                .setParameter("email", email)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<Account> findById(long accountId) {

        String query = "select m from Account m where m.id = :accountId";

        List<Account> result = em.createQuery(query, Account.class)
                .setParameter("accountId", accountId)
                .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }


    public boolean existsByEmail(String email) {

        String query = "select count(m) from Account m where m.email = :email";

        Long count = em.createQuery(query, Long.class)
                .setParameter("email", email)
                .getSingleResult();

        return count > 0;

    }

}
