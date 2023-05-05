package com.example.miniproject.config.security;

import com.example.miniproject.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImp implements UserDetails {

    private final User user; // 인증 완료된 User 객체
    private final String userId; // 인증 완료된 User 의 ID
    private final String password; // 인증 완료된 User 의 pw

    public UserDetailsImp(User user, String userId, String password) {
        this.user = user;
        this.userId = userId;
        this.password = password;
    }

    public User user() {
        return user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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
