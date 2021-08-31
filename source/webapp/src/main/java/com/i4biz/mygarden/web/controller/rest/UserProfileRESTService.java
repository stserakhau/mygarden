package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserProfile;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.IUserProfileService;
import com.i4biz.mygarden.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/userProfile")
public class UserProfileRESTService {
    @Autowired
    private IUserService userService;

    @Autowired
    private IUserProfileService userProfileService;


    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public UserProfile getUser(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        return userProfileService.findById(userId);
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(Principal principal, String oldPassword, String newPassword) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userService.resetPassword(userId, oldPassword, newPassword);
    }

    @RequestMapping(value = "/updateLocation", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateLocation(Principal principal, @RequestParam String country, @RequestParam String city) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userProfileService.updateLocation(userId, country, city);
    }
}
