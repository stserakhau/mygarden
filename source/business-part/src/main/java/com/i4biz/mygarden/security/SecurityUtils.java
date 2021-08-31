package com.i4biz.mygarden.security;

import com.i4biz.mygarden.domain.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static Long getAuthorizedUserId() {
        return getAuthorizedUser().getId();
    }

    public static User getAuthorizedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ApplicationPrincipal ap = auth == null ? null : (auth.getPrincipal() instanceof ApplicationPrincipal ? (ApplicationPrincipal) auth.getPrincipal() : null);
        return ap == null ? null : ap.getUser();
    }
}
