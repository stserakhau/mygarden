package com.i4biz.mygarden.web.listener;

import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.service.IUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class AnonymousUserProfileCreator implements HttpSessionListener {
    public static String USER_SESSION_ATTR = "anonymousUser";

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        ApplicationContext ctx = WebApplicationContextUtils.
                getWebApplicationContext(session.getServletContext());

        IUserService userService = ctx.getBean(IUserService.class);
        String sessionId = session.getId();
        User user = new User();
        user.setEmail(sessionId);
        user.setPassword(sessionId);
        session.setAttribute(USER_SESSION_ATTR, user);
        userService.registerAnonymousUser(user);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }
}
