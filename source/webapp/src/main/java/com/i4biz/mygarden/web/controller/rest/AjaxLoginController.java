package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserView;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class AjaxLoginController {
    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository repository;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/createAnonymousAndLogin", method = RequestMethod.POST)
    @ResponseBody
    public UserView createAnonymousAndLogin(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getSession().getId();
        String email = sessionId;
        String password = sessionId;

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userService.registerAnonymousUser(user);

        return login(email, password, sessionId, request, response);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public UserView login(@RequestParam String email, @RequestParam String password, @RequestParam String anonymousUsername,
                          HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                email,
                password
        );

        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        repository.saveContext(SecurityContextHolder.getContext(), request, response);
        rememberMeServices.loginSuccess(request, response, auth);

        ApplicationPrincipal user = (ApplicationPrincipal)auth.getPrincipal();

        UserView usr = userService.findViewById(user.getUser().getId());
        if (StringUtils.isNotEmpty(anonymousUsername) && !anonymousUsername.equals(email)) {
            userService.mergeAnonymousToUser(usr.getId(), anonymousUsername);
        }
        return usr;
    }

    @RequestMapping(value = "/is_authenticated", method = RequestMethod.GET)
    @ResponseBody
    public UserView isAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            return null;
        }

        ApplicationPrincipal ap = (ApplicationPrincipal)principal;
        Long userId = ap.getUser().getId();
        return userService.findViewById(userId);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}
