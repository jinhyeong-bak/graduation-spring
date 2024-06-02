package com.example.demo.domain.account;

import com.example.demo.dto.oauth.OAuthProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@ToString
@Entity
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    private String name;

    private String email;

    private String password;

    private Integer wrongPasswordCount;

    private LocalDateTime loginLastTryTime;

    private LocalDateTime loginLockTime;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public static Account createSignUpMember(String name, String email, String encodedPassword, OAuthProvider oAuthProvider) {
        Account account = new Account();
        account.name = name;
        account.email = email;
        account.password = encodedPassword;
        account.wrongPasswordCount = 0;
        account.loginLastTryTime = LocalDateTime.of(1,1, 1, 1, 1);
        account.loginLockTime = LocalDateTime.of(1,1, 1, 1, 1);
        account.oAuthProvider = oAuthProvider;
        return account;
    }

    public static Account createOAuthSignUpMember(String email, OAuthProvider oAuthProvider) {
        Account account = new Account();
        account.email = email;
        account.oAuthProvider = oAuthProvider;
        return account;
    }

    public static Account createLoginAccount(Long pk) {
        Account account = new Account();
        account.id = pk;
        return account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public Integer getWrongPasswordCount() {
        return wrongPasswordCount;
    }

    public void incWrongPasswordCount() {
        wrongPasswordCount++;
    }

    public void initWrongPasswordCount() {
        wrongPasswordCount = 0;
    }

    public void LockAccount() {
        loginLockTime = LocalDateTime.now();
    }

    public void renewLoginLastTryTime() {
        loginLastTryTime = LocalDateTime.now();
    }

    public LocalDateTime getLoginLastTryTime() {
        return loginLastTryTime;
    }

    public void renewalPassword(String password) {
        this.password = password;
    }

}
