package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.task.Task;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionNodeUtils;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.ISpeciesService;
import com.i4biz.mygarden.service.ISpeciesTaskFertilizerService;
import com.i4biz.mygarden.service.ITaskService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskRESTService {
    @Autowired
    private ITaskService taskService;

    @Autowired
    private ISpeciesService speciesService;

    @Autowired
    private ISpeciesTaskFertilizerService speciesTaskService;

    @RequestMapping(value = "/system/scroll", method = RequestMethod.GET)
    @ResponseBody
    public List<Task> systemScroll(HttpServletRequest request) {
        PageRequest<Task> pageRequest = PageRequestUtils
                .buildPageRequest(Task.class, request.getParameterMap());

        ExpressionNode root = new ExpressionNode("ownerId", "isNull", "");

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return taskService.scroll(pageRequest);
    }

    @RequestMapping(value = "/users/scroll", method = RequestMethod.GET)
    @ResponseBody
    public List<Task> usersScroll(Principal principal, HttpServletRequest request) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        PageRequest<Task> pageRequest = PageRequestUtils
                .buildPageRequest(Task.class, request.getParameterMap());

        ExpressionNode root = new ExpressionNode("ownerId", "eq", userId);

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return taskService.scroll(pageRequest);
    }

    @RequestMapping(value = "/available/page", method = RequestMethod.GET)
    @ResponseBody
    public PageResponse<Task> availablePage(Principal principal, HttpServletRequest request) {
        ExpressionNode root;
        if (principal == null) {
            root = new ExpressionNode("ownerId", "isNull", "");
        } else {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            Long userId = user.getId();
            root = ExpressionNodeUtils.or(
                    new ExpressionNode("ownerId", "eq", userId),
                    new ExpressionNode("ownerId", "isNull", "")
            );
        }

        PageRequest<Task> pageRequest = PageRequestUtils
                .buildPageRequest(Task.class, request.getParameterMap());

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return taskService.page(pageRequest);
    }

    @RequestMapping(value = "/available/scroll", method = RequestMethod.GET)
    @ResponseBody
    public List<Task> availableScroll(Principal principal, HttpServletRequest request) {
        Long userId = null;
        if (principal != null) {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            userId = user.getId();
        }

        PageRequest<Task> pageRequest = PageRequestUtils
                .buildPageRequest(Task.class, request.getParameterMap());

        ExpressionNode root = new ExpressionNode("ownerId", "isNull", "");
        if (userId != null) {
            root = root.or("ownerId", "eq", userId);
        }

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return taskService.scroll(pageRequest);
    }


    @RequestMapping(value = "/users/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public Task getTask(Principal principal, @PathVariable Long taskId) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        Task task = taskService.findById(taskId);

        return task;
    }

    @RequestMapping(value = "/users/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createUserTask(Principal principal, @RequestBody Task task) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        task.setOwnerId(userId);
        try {
            taskService.saveOrUpdate(task);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.work.exists");
        }  catch (RuntimeException e) {
            throw e;
        }
    }

    @RequestMapping(value = "/users/{taskId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateUserTask(Principal principal, @PathVariable Long taskId, @RequestBody Task task) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        task.setId(taskId);
        task.setOwnerId(userId);
        try {
            taskService.saveOrUpdate(task);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.work.exists");
        }
    }

    @RequestMapping(value = "/users/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(Principal principal, @PathVariable Long taskId) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        taskService.deleteTask(taskId, userId);
    }

    @RequestMapping(value = "/{taskId}/species", method = RequestMethod.GET)
    @ResponseBody
    public List<Item> scroll(Principal principal, @PathVariable Long taskId) {
        Long userId = null;
        if (principal != null) {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            userId = user.getId();
        }

        return speciesService.findSpeciesUsedInTask(taskId, userId);
    }
}