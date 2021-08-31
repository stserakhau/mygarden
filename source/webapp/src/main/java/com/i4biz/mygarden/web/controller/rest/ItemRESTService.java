package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizer;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizerView;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionNodeUtils;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.ICatalogService;
import com.i4biz.mygarden.service.IItemService;
import com.i4biz.mygarden.service.ISpeciesTaskFertilizerService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/item")
public class ItemRESTService {
    @Autowired
    private ICatalogService catalogService;
    @Autowired
    private IItemService itemService;

    @Autowired
    private ISpeciesTaskFertilizerService speciesTaskFertilizerService;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public PageResponse<ItemView> page(HttpServletRequest request) {
        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        return itemService.page(pageRequest);
    }

    @RequestMapping(value = "/available/page", method = RequestMethod.GET)
    @ResponseBody
    public PageResponse<ItemView> availablePage(Principal principal, HttpServletRequest request) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        ExpressionNode root = ExpressionNodeUtils.or(
                new ExpressionNode("ownerId", "eq", userId),
                new ExpressionNode("ownerId", "isNull", "")
        );

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return itemService.page(pageRequest);
    }

    @RequestMapping(value = "/available/scroll", method = RequestMethod.GET)
    @ResponseBody
    public List<ItemView> availableScroll(Principal principal, HttpServletRequest request) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        ExpressionNode root = ExpressionNodeUtils.or(
                new ExpressionNode("ownerId", "eq", userId),
                new ExpressionNode("ownerId", "isNull", "")
        );

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return itemService.scroll(pageRequest);
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET)
    @ResponseBody
    public List<ItemView> scrollMy(HttpServletRequest request, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode filterCondition = new ExpressionNode("ownerId", "eq", userId);

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return itemService.scroll(pageRequest);
    }

    @RequestMapping(value = "/{fertilizerId}", method = RequestMethod.GET)
    @ResponseBody
    public IItemService.ItemForm findById(@PathVariable Long itemId) {
        Item item = itemService.findById(itemId);
        Catalog catalog = catalogService.findById(item.getCatalogId());
        return new IItemService.ItemForm(catalog, item);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Item createSpecies(@RequestBody IItemService.ItemForm itemForm, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        itemForm.catalog.setOwnerId(userId);
        itemForm.item.setOwnerId(userId);
        try {
            Item item = itemService.createItem(itemForm);
            return item;
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.item.exists");
        }
    }

    @RequestMapping(value = "/{fertilizerId}/delete", method = RequestMethod.POST)
    public View deleteFertilizer(@PathVariable Long fertilizerId, @RequestParam(defaultValue = "false") Boolean deleteClass, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        itemService.delete(fertilizerId, userId, deleteClass);
        return new RedirectView("/fertilizers_inventory.tiles"); //todo error - species & fertilizers but fertilizer only
    }

    @RequestMapping(value = "/{fertilizerId}/resetFertilizerUserSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void resetFertilizerUserSettings(@PathVariable Long fertilizerId, Principal principal) {
        //todo need to check fertilizer group before execution
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        itemService.resetFertilizerUserSettings(fertilizerId, userId);
    }

    @RequestMapping(value = "/{fertilizerId}/species", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Item> findFertilizerSpecies(@PathVariable Long fertilizerId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return itemService.findAvailableSpeciesLinkedWithFertilizer(fertilizerId, userId);
    }

    @RequestMapping(value = "/{fertilizerId}/species/{speciesId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createSpeciesTaskFertilizer(@PathVariable Long fertilizerId, @PathVariable Long speciesId, @RequestBody SpeciesTaskFertilizer speciesTaskFertilizer, Principal principal) {
        try {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            Long userId = user.getId();
            speciesTaskFertilizer.setOwnerId(userId);
            speciesTaskFertilizer.setSpeciesId(speciesId);
            speciesTaskFertilizer.setFertilizerId(fertilizerId);
            speciesTaskFertilizerService.saveOrUpdate(speciesTaskFertilizer);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("error.fertilizer.species.duplicateSettings");
        }
    }

    @RequestMapping(value = "/{fertilizerId}/species/{speciesId}", method = RequestMethod.GET)
    @ResponseBody
    public Collection<SpeciesTaskFertilizerView> fertilizerSpecies(@PathVariable Long fertilizerId, @PathVariable Long speciesId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();

        Long userId = user == null ? null : user.getId();

        ExpressionNode root = new ExpressionNode(
                new ExpressionNode("fertilizerId", "eq", fertilizerId),
                new ExpressionNode("speciesId", "eq", speciesId),
                "and"
        );
        if (userId != null) {
            root = ExpressionNodeUtils.and(
                    root,
                    new ExpressionNode("ownerId", "eq", userId)
            );
        } else {
            root = ExpressionNodeUtils.and(
                    root,
                    new ExpressionNode("ownerId", "isNull", null)
            );
        }

        PageRequest<SpeciesTaskFertilizerView> pr = new PageRequest<>(
                0,
                1000,
                root,
                new HashMap<String, String>() {{
                    put("taskName", "asc");
                }}
        );

        return speciesTaskFertilizerService.scroll(pr);
    }

    @RequestMapping(value = "/{fertilizerId}/species_task/{speciesTaskId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void fertilizerSpeciesDelete(@PathVariable Long fertilizerId, @PathVariable Long speciesTaskId) {
        speciesTaskFertilizerService.delete(fertilizerId, speciesTaskId);
    }
}