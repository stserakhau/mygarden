package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.user.NotificationSettings;
import com.i4biz.mygarden.domain.user.RoleEnum;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserView;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.IUserService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserRESTService {
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public PageResponse<UserView> getPage(HttpServletRequest request) {
        PageRequest<UserView> pageRequest = PageRequestUtils
                .buildPageRequest(UserView.class, request.getParameterMap());
        return userService.page(pageRequest);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public User getUser(@PathVariable long userId) {
        return userService.findById(userId);
    }

    @RequestMapping(value = "/current/", method = RequestMethod.GET)
    @ResponseBody
    public IUserService.RegistrationData currentUser(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        IUserService.RegistrationData data = userService.loadRegistrationData(userId);
        data.user.setPassword(null);
        data.user.setRole(null);
        return data;
    }

    @RequestMapping(value = "/current/", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void currentUser(Principal principal, @RequestBody IUserService.RegistrationData data) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        data.user.setId(userId);
        data.user.setRole(RoleEnum.ROLE_USER);
        data.userProfile.setId(userId);
        userService.saveRegistrationData(data);
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public User registerUser(@RequestBody IUserService.RegistrationData rr, HttpSession httpSession) {
        User anonymousUser = (User) httpSession.getAttribute(User.class.getName());
        if (anonymousUser != null) {
            rr.user.setId(anonymousUser.getId());
        }
        rr.user.setRole(RoleEnum.ROLE_USER);

        User user = userService.registerUser(rr);

        httpSession.invalidate();

        return user;
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    @ResponseBody
    public void forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
    }

    @RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
    public View confirmRegistration(@RequestParam String registrationCode) {
        userService.confirmRegistration(registrationCode);

        return new RedirectView("/user_reset_password.tiles?registrationCode=" + registrationCode);
    }

    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public View resetPassword(@RequestParam String registrationCode, @RequestParam String password) {
        userService.resetPassword(registrationCode, password);

        return new RedirectView("/homepage.tiles");
    }

    @RequestMapping(value = "/current/notification_settings", method = RequestMethod.GET)
    @ResponseBody
    public NotificationSettings findNotificationSettings(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return userService.findNotificationSetting(userId);
    }

    @RequestMapping(value = "/current/notification_settings", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void saveNotificationSettings(Principal principal, @RequestBody NotificationSettings notificationSettings) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        notificationSettings.setId(userId);
        userService.updateNotificationSettings(notificationSettings);
    }
}
