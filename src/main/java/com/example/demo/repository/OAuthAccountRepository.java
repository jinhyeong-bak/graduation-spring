package com.example.demo.repository;

import com.example.demo.domain.OAuthAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class OAuthAccountRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<OAuthAccount> findByEmail(String email) {
        String query = "select o from OAuthAccount o where email =: email";
        List<OAuthAccount> result = em.createQuery(query, OAuthAccount.class)
                .setParameter("email", email)
                .getResultList();

        return result.size() == 1 ? Optional.of(result.get(0)) : Optional.empty();
    }

    public Long save(OAuthAccount oAuthAccount) {
        em.persist(oAuthAccount);
        return oAuthAccount.getId();
    }
}
