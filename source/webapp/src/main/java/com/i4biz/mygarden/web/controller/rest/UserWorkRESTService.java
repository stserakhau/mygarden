package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.domain.user.PatternAuthor;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserWork;
import com.i4biz.mygarden.domain.user.UserWorkView;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionNodeUtils;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.IUserWorkService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import com.i4biz.mygarden.web.listener.AnonymousUserProfileCreator;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Controller
@RequestMapping("/user/work")
public class UserWorkRESTService {
    @Autowired
    private IUserWorkService userWorkService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Collection<UserWorkView> getEventsPage(Principal principal, HttpServletRequest request) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        PageRequest<UserWorkView> pageRequest = PageRequestUtils
                .buildPageRequest(UserWorkView.class, request.getParameterMap());


        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode filterCondition = new ExpressionNode("userId", "eq", userId);

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return userWorkService.scroll(pageRequest);
    }

    @RequestMapping(value = "/system/", method = RequestMethod.GET)
    @ResponseBody
    public Collection<UserWorkView> getSystemTemplates(HttpServletRequest request) {
        PageRequest<UserWorkView> pageRequest = PageRequestUtils
                .buildPageRequest(UserWorkView.class, request.getParameterMap());


        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode filterCondition = ExpressionNodeUtils.or(
                new ExpressionNode("userId", "isNull", ""),
                new ExpressionNode("authorId", "isNotNull", "")
        );

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return userWorkService.scroll(pageRequest);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Long saveUserWork(Principal principal, @RequestBody UserWork userWork) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userWork.setUserId(userId);
        userWork.setPattern(false);
        try {
            userWorkService.saveOrUpdate(userWork);
            return userWork.getId();
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.pattern.exists");
        }
    }

    @RequestMapping(value = "/pattern", method = RequestMethod.POST)
    @ResponseBody
    public Long saveUserPattern(Principal principal, @RequestBody UserWork userWork) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userWork.setUserId(userId);
        userWork.setPattern(true);
        try {
            userWorkService.saveOrUpdate(userWork);
            return userWork.getId();
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.pattern.exists");
        }
    }

    @RequestMapping(value = "/pattern/clone", method = RequestMethod.POST)
    @ResponseBody
    public UserWork cloneUserPattern(Principal principal, @RequestBody UserWork userWork) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userWork.setUserId(userId);
        try {
            userWorkService.cloneUserWorkAsPattern(userWork);

            return userWork;
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.pattern.exists");
        }
    }

    @RequestMapping(value = "/pattern/{patternId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void quickBuildUserWorkFromPattern(Principal principal, @PathVariable Long patternId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        userWorkService.quickBuildUserWorkFromPattern(userId, patternId, new java.sql.Date(startDate.getTime()));
    }

    @RequestMapping(value = "/pattern/init", method = RequestMethod.POST)
    @ResponseBody
    public Long initUserWorkFromPattern(Principal principal, @RequestBody UserWork userWork, HttpSession httpSession) {
        User user;
        if (principal != null) {
            user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        } else {
            user = (User) httpSession.getAttribute(AnonymousUserProfileCreator.USER_SESSION_ATTR);
        }
        Long userId = user.getId();
        userWork.setUserId(userId);
        try {
            return userWorkService.initUserWork(userWork);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.pattern.exists");
        }
    }

    @RequestMapping(value = "/{userWorkId}", method = RequestMethod.GET)
    @ResponseBody
    public UserWork findById(Principal principal, @PathVariable Long userWorkId) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        UserWork uw = userWorkService.findById(userWorkId);
        if (uw.getUserId() != userId) {
            throw new RuntimeException("Access denied for " + userId + " to " + userWorkId);
        }
        return uw;
    }

    @RequestMapping(value = "/{userWorkId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserWork(Principal principal, @PathVariable Long userWorkId) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userWorkService.deleteUserWork(userId, userWorkId);
    }

    @RequestMapping(value = "/authors/system/", method = RequestMethod.GET)
    @ResponseBody
    public Collection<PatternAuthor> getAllAuthors() {
        return userWorkService.getAllAuthors();
    }


}