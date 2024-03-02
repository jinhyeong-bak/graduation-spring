package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Entity
public class Account implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public static Account createSignUpMember(String name, String email, String encodedPassword) {
        Account account = new Account();
        account.name = name;
        account.email = email;
        account.password = encodedPassword;
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
}
