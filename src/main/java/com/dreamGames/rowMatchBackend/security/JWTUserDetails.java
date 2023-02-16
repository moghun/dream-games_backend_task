package com.dreamGames.rowMatchBackend.security;

import com.dreamGames.rowMatchBackend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JWTUserDetails extends User implements UserDetails {
    public Long ID;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public JWTUserDetails(User user){
        super(user);
    }

    private JWTUserDetails(Long ID, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static JWTUserDetails create(User user) {
        List<GrantedAuthority> authoritiesList = new ArrayList<>();
        authoritiesList.add(new SimpleGrantedAuthority("user"));
        return new JWTUserDetails(user.getID(), user.getUsername(), user.getPassword(), authoritiesList);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
