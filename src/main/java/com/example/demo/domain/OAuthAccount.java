package com.example.demo.domain;

import com.example.demo.dto.oauth.OAuthProvider;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class OAuthAccount {
    @Id @GeneratedValue
    private Long id;
    private String email;
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @Builder
    public OAuthAccount(String email, OAuthProvider oAuthProvider) {
        this.email = email;
        this.oAuthProvider = oAuthProvider;
    }

}
