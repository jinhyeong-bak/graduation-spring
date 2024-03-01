package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Entity
public class Member implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public static Member createSignUpMember(String name, String email, String encodedPassword) {
        Member member = new Member();
        member.name = name;
        member.email = email;
        member.password = encodedPassword;
        return member;
    }

    public static Member createLoginMember(Long pk) {
        Member member = new Member();
        member.id = pk;
        return member;
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
