package com.sparta.hanghaeboard.security;

import com.sparta.hanghaeboard.entity.User;
import com.sparta.hanghaeboard.entity.UserRoleEnum;

public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final String username;
    private final String password;

    public UserDetailsImpl(User user, String username, String password) {
        this.user = user;
        this.username = username;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
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