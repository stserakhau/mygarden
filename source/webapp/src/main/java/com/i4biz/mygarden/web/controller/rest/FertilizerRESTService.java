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
import com.i4biz.mygarden.service.*;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/fertilizer")
public class FertilizerRESTService {
    @Autowired
    private IFertilizerService fertilizerService;

    @Autowired
    private ICatalogService catalogService;

    @Autowired
    private ISpeciesTaskFertilizerService speciesTaskFertilizerService;

    @Autowired
    private ISpeciesService speciesService;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public PageResponse<ItemView> page(HttpServletRequest request) {
        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        return fertilizerService.page(pageRequest);
    }

    @RequestMapping(value = "/available/page", method = RequestMethod.GET)
    @ResponseBody
    public PageResponse<ItemView> availablePage(Principal principal, HttpServletRequest request) {
        Long userId;
        if (principal == null) {
            userId = null;
        } else {
            userId = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser().getId();
        }


        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        ExpressionNode root = new ExpressionNode("ownerId", "isNull", "");
        if (userId != null) {
            root = ExpressionNodeUtils.or(
                    new ExpressionNode("ownerId", "eq", userId),
                    root
            );
        }
        {
            List<Catalog> catalogs = catalogService.getAvailableCatalogItems(Catalog.FERTILIZER_GROUP_ID, userId);
            List<Long> catalogIds = new ArrayList<>(catalogs.size());
            for (Catalog c : catalogs) {
                catalogIds.add(c.getId());
            }
            if (!catalogIds.isEmpty()) {
                root = ExpressionNodeUtils.and(
                        root,
                        new ExpressionNode("catalogId", "in", catalogIds)
                );
            }
        }

        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return fertilizerService.page(pageRequest);
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
        {
            List<Catalog> catalogs = catalogService.getAvailableCatalogItems(Catalog.FERTILIZER_GROUP_ID, userId);
            List<Long> catalogIds = new ArrayList<>(catalogs.size());
            for (Catalog c : catalogs) {
                catalogIds.add(c.getId());
            }
            if (!catalogIds.isEmpty()) {
                root = ExpressionNodeUtils.and(
                        root,
                        new ExpressionNode("catalogId", "in", catalogIds)
                );
            }
        }
        ExpressionNode en = pageRequest.getConditionTree();
        if (en != null) {
            root = ExpressionNodeUtils.and(root, en);
        }
        pageRequest.setConditionTree(root);

        return fertilizerService.scroll(pageRequest);
    }

    @RequestMapping(value = "/{fertilizerId}", method = RequestMethod.GET)
    @ResponseBody
    public IItemService.ItemForm findById(@PathVariable Long fertilizerId) {
        Item f = fertilizerService.findById(fertilizerId);
        Catalog fc = catalogService.findById(f.getCatalogId());
        return new IItemService.ItemForm(fc, f);
    }

    @RequestMapping(value = "/restrictions/{fertilizerId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Item> findFertilizerRestrictions(@PathVariable Long fertilizerId, Principal principal) {
        Long userId = null;
        if (principal != null) {
            userId = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser().getId();
        }
        return fertilizerService.findFertilizerRestrictions(fertilizerId, userId);
    }

    @RequestMapping(value = "/restrictions/", method = RequestMethod.POST)
    @ResponseBody
    public List<Item> findFertilizerRestrictions(@RequestBody Long[] fertilizerId, Principal principal) {
        Long userId = null;
        if (principal != null) {
            userId = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser().getId();
        }
        return fertilizerService.findFertilizerRestrictions(fertilizerId, userId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Item createItem(@RequestBody IFertilizerService.FertilizerForm fertilizerForm, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        fertilizerForm.catalog.setOwnerId(userId);
        fertilizerForm.item.setOwnerId(userId);
        try {
            return fertilizerService.createFertilizer(fertilizerForm);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.fertilizer.exists");
        }
    }

    @RequestMapping(value = "/{fertilizerId}/delete", method = RequestMethod.POST)
    public View deleteItem(@PathVariable Long fertilizerId, @RequestParam(defaultValue = "false") Boolean deleteClass, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        fertilizerService.delete(fertilizerId, userId, deleteClass);
        return new RedirectView("/fertilizers_inventory.tiles");
    }

    @RequestMapping(value = "/resetUserSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void resetUserSettings(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        fertilizerService.resetFertilizerUserSettings(userId);
    }

    @RequestMapping(value = "/{fertilizerId}/resetUserSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void resetUserSettings(@PathVariable Long fertilizerId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        fertilizerService.resetFertilizerUserSettings(fertilizerId, userId);
    }


    @RequestMapping(value = "/catalog", method = RequestMethod.GET)
    @ResponseBody
    public List<Catalog> catalogList(Principal principal) {
        Long userId = null;
        if (principal != null) {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            userId = user.getId();
        }
        return catalogService.getAvailableCatalogItems(Catalog.FERTILIZER_GROUP_ID, userId);
    }

    @RequestMapping(value = "/catalog/{catalogId}", method = RequestMethod.GET)
    @ResponseBody
    public Catalog catalog(@PathVariable Long catalogId) {
        return catalogService.findById(catalogId);
    }

    @RequestMapping(value = "/catalog/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createCatalog(@RequestBody Catalog catalog, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalog.setParentId(Catalog.FERTILIZER_GROUP_ID);
        catalog.setOwnerId(userId);
        try {
            catalogService.saveOrUpdate(catalog);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.fertilizerCatalog.exists");
        }
    }

    @RequestMapping(value = "/catalog/{catalogId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void saveCatalog(@PathVariable Long catalogId, @RequestBody Catalog catalog, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalog.setId(catalogId);
        catalog.setParentId(Catalog.FERTILIZER_GROUP_ID);
        catalog.setOwnerId(userId);
        try {
            catalogService.saveOrUpdate(catalog);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.fertilizerCatalog.exists");
        }
    }

    @RequestMapping(value = "/catalog/{catalogId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCatalog(@PathVariable Long catalogId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        fertilizerService.deleteCatalog(catalogId, userId);
    }

    @RequestMapping(value = "/{fertilizerId}/species", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Item> fertilizerSpecies(@PathVariable Long fertilizerId, Principal principal) {
        Long userId = null;
        if (principal != null) {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            userId = user.getId();
        }
        return speciesService.findAvailableSpeciesLinkedWithFertilizer(fertilizerId, userId);
    }

    @RequestMapping(value = "/{fertilizerId}/species/{speciesId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createSpeciesTaskItem(@PathVariable Long fertilizerId, @PathVariable Long speciesId, @RequestBody SpeciesTaskFertilizer speciesTaskFertilizer, Principal principal) {
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
        Long userId = null;
        if (principal != null) {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            userId = user.getId();
        }

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