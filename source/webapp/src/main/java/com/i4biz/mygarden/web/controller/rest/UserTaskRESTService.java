package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.task.UserTask;
import com.i4biz.mygarden.domain.user.task.UserTaskView;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.IUserTaskService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import com.i4biz.mygarden.web.listener.AnonymousUserProfileCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

@Controller
@RequestMapping("/user/task")
public class UserTaskRESTService {
    @Autowired
    private IUserTaskService userTaskService;

    @RequestMapping(value = "/scroll", method = RequestMethod.GET)
    @ResponseBody
    public Collection<UserTaskView> scroll(Principal principal, HttpServletRequest request, HttpSession httpSession) {
        User user;
        if (principal != null) {
            user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        } else {
            user = (User) httpSession.getAttribute(AnonymousUserProfileCreator.USER_SESSION_ATTR);
        }
        Long userId = user.getId();

        PageRequest<UserTaskView> pageRequest = PageRequestUtils
                .buildPageRequest(UserTaskView.class, request.getParameterMap());


        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode filterCondition = new ExpressionNode("ownerId", "eq", userId);

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return userTaskService.scroll(pageRequest);
    }

    @RequestMapping(value = "/scroll/exportCSV", method = RequestMethod.GET)
    public void scrollExportCSV(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"mycalendar.csv\"");
        Collection<UserTaskView> userTasks = this.scroll(principal, request, httpSession);
        StringBuilder sb = new StringBuilder(2048);
        sb.append("Дата;Культура;Работа;Описание;Удобрения\n");
        SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");
        for (UserTaskView utv : userTasks) {
            String species = '"' + utv.getSpeciesName().replaceAll("\"", "\\\"") + '\"';

            String task = '"' + utv.getTaskName().replaceAll("\"", "\\\"") + '\"';

            String comment = '"' + utv.getTaskComment().replaceAll("\"", "\\\"") + '\"';

            sb
                    .append(SDF.format(utv.getCalculatedDate())).append(";")
                    .append(species).append(";")
                    .append(task).append(";")
                    .append(comment).append(";");

            Iterator<Item> iterator = utv.getFertilizers().iterator();
            while (iterator.hasNext()) {
                Item fert = iterator.next();
                sb.append(fert.getName());
                if (iterator.hasNext()) {
                    sb.append(',');
                }
            }

            sb.append("\n");
        }

        response.getOutputStream().write(sb.toString().getBytes());
    }

    @RequestMapping(value = "/scroll/exportICS", method = RequestMethod.GET)
    public void scrollExportICS(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response, HttpSession httpSession) throws IOException {
        response.setContentType("text/calendar");
        response.setHeader("Content-Disposition", "attachment; filename=\"mycalendar.ics\"");
        Collection<UserTaskView> userTasks = this.scroll(principal, request, httpSession);

        StringBuilder sb = new StringBuilder(2048);

        sb
                .append("BEGIN:VCALENDAR\n")
                .append("VERSION:2.0\n")
                .append("PRODID:-//hacksw/handcal//NONSGML v1.0//EN'n");


        SimpleDateFormat DF = new SimpleDateFormat("yyyyMMdd'THHmmss'Z");
        for (UserTaskView utv : userTasks) {
            sb
                    .append("BEGIN:VEVENT\n")
                    .append("UID:").append(utv.getId())
                    .append("\nDTSTAMP:").append(DF.format(utv.getCalculatedDate()))
                    .append("\nORGANIZER:").append("https://egardening.ru")
                    .append("\nDTSTART:").append(DF.format(utv.getCalculatedDate()))
                    .append("\nDTEND:").append(DF.format(utv.getCalculatedDate()))
                    .append("\nSUMMARY:");

            {
                String species = '"' + utv.getSpeciesName().replaceAll("\"", "\\\"") + '\"';

                String task = '"' + utv.getTaskName().replaceAll("\"", "\\\"") + '\"';

                String comment = '"' + utv.getTaskComment().replaceAll("\"", "\\\"") + '\"';

                sb
                        .append(species).append(" \\n ")
                        .append(task).append(" \\n ")
                        .append(comment).append(" \\n ");

                Iterator<Item> iterator = utv.getFertilizers().iterator();
                while (iterator.hasNext()) {
                    Item fert = iterator.next();
                    sb.append(fert.getName());
                    if (iterator.hasNext()) {
                        sb.append(',');
                    }
                }


            }
            sb.append("\nEND:VEVENT");
        }
        sb.append("\nEND:VCALENDAR");

        response.getOutputStream().write(sb.toString().getBytes());
    }

    @RequestMapping(value = "/{userWorkId}/all", method = RequestMethod.GET)
    @ResponseBody
    public Collection<UserTask> all(@PathVariable Long userWorkId, @RequestParam(required = false, defaultValue = "false") Boolean system, Principal principal, HttpSession httpSession) {
        User user;
        if (principal != null) {
            user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        } else {
            user = (User) httpSession.getAttribute(AnonymousUserProfileCreator.USER_SESSION_ATTR);
        }
        Long ownerId = user.getId();

        if (system) {
            return userTaskService.findSystemUserTasksByUserWork(userWorkId);
        } else {
            return userTaskService.findUserTasksByUserWork(ownerId, userWorkId);
        }
    }

    @RequestMapping(value = "/{userTaskId}", method = RequestMethod.GET)
    @ResponseBody
    public UserTask findById(@PathVariable Long userTaskId, Principal principal, HttpSession httpSession) {
        User user;
        if (principal != null) {
            user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        } else {
            user = (User) httpSession.getAttribute(AnonymousUserProfileCreator.USER_SESSION_ATTR);
        }
        Long ownerId = user.getId();

        return userTaskService.findById(userTaskId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Long save(Principal principal, @RequestBody UserTask userTask, HttpSession httpSession) {
        User user;
        if (principal != null) {
            user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        } else {
            user = (User) httpSession.getAttribute(AnonymousUserProfileCreator.USER_SESSION_ATTR);
        }
        Long userId = user.getId();
        userTask.setOwnerId(userId);
        userTaskService.saveOrUpdate(userTask);
        return userTask.getId();
    }

    @RequestMapping(value = "/{userTaskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(Principal principal, @PathVariable Long userTaskId) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userTaskService.deleteUserTask(userId, userTaskId);
    }

}