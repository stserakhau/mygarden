package com.i4biz.mygarden.security;

import com.i4biz.mygarden.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;

public class ApplicationPrincipal extends org.springframework.security.core.userdetails.User {
    private final User user;

    public ApplicationPrincipal(final User user) {
        super(user.getEmail(), user.getPassword(), new ArrayList<GrantedAuthority>() {{
            add(new SimpleGrantedAuthority(user.getRole().name()));
        }});
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
