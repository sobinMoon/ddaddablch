package com.donation.ddb.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String nickname;
    private final String role; // "ROLE_STUDENT" 또는 "ROLE_ORGANIZATION"
    //private final Collection<? extends GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password; // JWT에서는 사용하지 않음
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // JWT 토큰이 유효하면 계정도 유효
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // JWT 토큰이 유효하면 계정 잠금 없음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // JWT 토큰 자체가 credential
    }

    @Override
    public boolean isEnabled() {
        return true; // JWT 토큰이 유효하면 활성화된 것
    }

    // 편의 메서드 - role을 기준으로 판단
    public boolean isStudent() {
        return "ROLE_STUDENT".equals(role);
    }

    public boolean isOrganization() {
        return "ROLE_ORGANIZATION".equals(role);
    }
}