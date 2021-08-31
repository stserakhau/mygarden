package com.i4biz.mygarden.security;

import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpSession;

public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private IUserService userService;

    @Autowired
    HttpSession httpSession;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        boolean isTemporaryUser = !user.getEmail().contains("@");

        if (!isTemporaryUser && !user.isRegistrationConfirmed()) {
            throw new UsernameNotFoundException("Registration not confirmed for user " + email);
        }
        userService.updateLoginDate(user.getId());
        httpSession.setAttribute(User.class.getName(), user);

        return new ApplicationPrincipal(user);
    }
}
