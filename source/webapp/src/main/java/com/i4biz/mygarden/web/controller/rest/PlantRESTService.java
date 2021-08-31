package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserPlant;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.IUserPlantService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/plant")
public class PlantRESTService {
    @Autowired
    private IUserPlantService userPlantService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<UserPlant> scroll(HttpServletRequest request, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        PageRequest<UserPlant> pageRequest = PageRequestUtils
                .buildPageRequest(UserPlant.class, request.getParameterMap());

        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode filterCondition = new ExpressionNode("ownerId", "eq", userId);

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return userPlantService.scroll(pageRequest);
    }

    @RequestMapping(value = "/{plantId}", method = RequestMethod.GET)
    @ResponseBody
    public UserPlant loadPlant(@PathVariable Long plantId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return userPlantService.findUserPlantById(plantId, userId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public UserPlant createPlant(@RequestBody UserPlant plant, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        plant.setOwnerId(userId);
        return userPlantService.saveOrUpdate(plant);
    }

    @RequestMapping(value = "/{plantId}", method = RequestMethod.PUT)
    @ResponseBody
    public UserPlant updatePlant(@PathVariable Long plantId, @RequestBody UserPlant plant, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        UserPlant up = userPlantService.findUserPlantById(plantId, userId);
        up.setName(plant.getName());
        up.setWidth(plant.getWidth());
        up.setHeight(plant.getHeight());

        return userPlantService.saveOrUpdate(up);
    }

    @RequestMapping(value = "/{plantId}/model", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updatePlantModel(@PathVariable Long plantId, @RequestBody Map model, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        userPlantService.updatePlantModel(plantId, userId, model);
    }
}
