package com.example.demo.infrastructure.jwt;

import com.example.demo.dto.oauth.OAuthProvider;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "TOKEN")
public class JwtEntity {
    @Id @GeneratedValue
    private Long id;
    private Long userPk;
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;
    private String refreshToken;

    public static JwtEntity createEntityWhenLogin(Long userPk, OAuthProvider oAuthProvider, String refreshToken) {
        JwtEntity je = new JwtEntity();
        je.userPk = userPk;
        je.refreshToken = refreshToken;
        je.oAuthProvider = oAuthProvider;
        return je;
    }
}
