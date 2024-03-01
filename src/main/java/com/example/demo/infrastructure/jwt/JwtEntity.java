package com.example.demo.infrastructure.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "TOKEN")
public class JwtEntity {
    @Id @GeneratedValue
    private Long id;
    private Long userPk;
    private String refreshToken;

    public static JwtEntity createEntityWhenLogin(Long userPk, String refreshToken) {
        JwtEntity je = new JwtEntity();
        je.userPk = userPk;
        je.refreshToken = refreshToken;
        return je;
    }
}
