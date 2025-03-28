package com.univolunteer.user.security;

import com.univolunteer.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public class LoginUser implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private UserRoleEnum role;
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
