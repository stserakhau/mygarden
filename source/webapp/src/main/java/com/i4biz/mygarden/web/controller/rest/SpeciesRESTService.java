package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizerView;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionNodeUtils;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.ICatalogService;
import com.i4biz.mygarden.service.ISpeciesCatalogService;
import com.i4biz.mygarden.service.ISpeciesService;
import com.i4biz.mygarden.service.ISpeciesTaskFertilizerService;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/species")
public class SpeciesRESTService {
    @Autowired
    private ISpeciesService speciesService;

    @Autowired
    private ICatalogService catalogService;

    @Autowired
    private ISpeciesCatalogService speciesCatalogService;

    @Autowired
    private ISpeciesTaskFertilizerService speciesTaskFertilizerService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<ItemView> scroll(HttpServletRequest request, Principal principal) {
        ExpressionNode filterCondition;
        Long userId = null;
        if (principal == null) {
            filterCondition = new ExpressionNode("ownerId", "isNull", "");
        } else {
            User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
            userId = user.getId();
            filterCondition = ExpressionNodeUtils.or(
                    new ExpressionNode("ownerId", "eq", userId),
                    new ExpressionNode("ownerId", "isNull", "")
            );
        }

        List<Catalog> catalogs = catalogService.getAvailableCatalogItems(Catalog.SPECIES_GROUP_ID, userId);
        if (!catalogs.isEmpty()) {
            List<Long> ids = new ArrayList<>(catalogs.size());
            ids.addAll(catalogs.stream().map(Catalog::getId).collect(Collectors.toList()));
            filterCondition = filterCondition.and(new ExpressionNode("catalogId", "in", ids));
        }

        PageRequest<ItemView> pageRequest = PageRequestUtils
                .buildPageRequest(ItemView.class, request.getParameterMap());

        ExpressionNode root = pageRequest.getConditionTree();

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return speciesService.scroll(pageRequest);
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

        List<Catalog> catalogs = catalogService.getAvailableCatalogItems(Catalog.SPECIES_GROUP_ID, userId);
        if (!catalogs.isEmpty()) {
            List<Long> ids = new ArrayList<>(catalogs.size());
            ids.addAll(catalogs.stream().map(Catalog::getId).collect(Collectors.toList()));
            filterCondition = filterCondition.and(new ExpressionNode("catalogId", "in", ids));
        }

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return speciesService.scroll(pageRequest);
    }

    @RequestMapping(value = "/{speciesId}", method = RequestMethod.GET)
    @ResponseBody
    public ISpeciesService.SpeciesForm findById(@PathVariable Long speciesId) {
        Item species = speciesService.findById(speciesId);
        Catalog speciesCatalog = speciesCatalogService.findById(species.getCatalogId());
        return new ISpeciesService.SpeciesForm(
                speciesCatalog,
                species
        );
    }

    @RequestMapping(value = "/{speciesId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteSpecies(@PathVariable Long speciesId, @RequestHeader(defaultValue = "false", required = false) Boolean removeCatalogIfEmpty, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        Item species = speciesService.findById(speciesId);
        speciesService.deleteSpecies(speciesId, userId);
        if (removeCatalogIfEmpty) {
            speciesCatalogService.deleteCatalogIfEmpty(species.getCatalogId(), userId);
        }
    }

    @RequestMapping(value = "/copySystemToMy/{systemSpeciesId}", method = RequestMethod.POST)
    @ResponseBody
    public Long copySystemToMy(@PathVariable Long systemSpeciesId, @RequestBody ISpeciesService.SpeciesForm speciesForm, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();

        speciesForm.speciesCatalog.setOwnerId(userId);
        speciesForm.species.setOwnerId(userId);

        try {
            try {
                return speciesService.copySystemSpeciesToMy(systemSpeciesId, speciesForm);
            } catch (RuntimeException e) {
                throw (ConstraintViolationException) e.getCause();
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.species.exists");
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Item createSpecies(@RequestBody ISpeciesService.SpeciesForm speciesForm, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        speciesForm.speciesCatalog.setOwnerId(userId);
        speciesForm.species.setOwnerId(userId);
        try {
            Item species = speciesService.createSpecies(speciesForm);
            return species;
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.species.exists");
        }
    }

    @RequestMapping(value = "/catalog/my", method = RequestMethod.GET)
    @ResponseBody
    public List<Catalog> myCatalogList(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return speciesCatalogService.getMyCatalogItems(userId);
    }

    @RequestMapping(value = "/catalog/available", method = RequestMethod.GET)
    @ResponseBody
    public List<Catalog> availableCatalogList(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return speciesCatalogService.getAvailableCatalogItems(userId);
    }

    @RequestMapping(value = "/catalog/{catalogId}", method = RequestMethod.GET)
    @ResponseBody
    public Catalog catalog(@PathVariable Long catalogId) {
        return speciesCatalogService.findById(catalogId);
    }

    @RequestMapping(value = "/catalog/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createCatalog(@RequestBody Catalog catalog, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalog.setOwnerId(userId);
        catalog.setParentId(Catalog.SPECIES_GROUP_ID);
        saveOrUpdate(catalog);
    }

    @RequestMapping(value = "/catalog/{catalogId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void saveCatalog(@PathVariable Long catalogId, @RequestBody Catalog catalog, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalog.setId(catalogId);
        catalog.setParentId(Catalog.SPECIES_GROUP_ID);
        catalog.setOwnerId(userId);
        saveOrUpdate(catalog);
    }

    private void saveOrUpdate(Catalog catalog) {
        try {
            speciesCatalogService.saveOrUpdate(catalog);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.speciesCatalog.exists");
        }
    }

    @RequestMapping(value = "/catalog/{catalogId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCatalog(@PathVariable Long catalogId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        speciesCatalogService.deleteCatalog(catalogId, userId);
    }

    @RequestMapping(value = "/{speciesId}/tasks", method = RequestMethod.GET)
    @ResponseBody
    public List<SpeciesTaskFertilizerView> scroll(@PathVariable Long speciesId, HttpServletRequest request) {
        PageRequest<SpeciesTaskFertilizerView> pageRequest = PageRequestUtils
                .buildPageRequest(SpeciesTaskFertilizerView.class, request.getParameterMap());


        ExpressionNode root = pageRequest.getConditionTree();

        ExpressionNode filterCondition = new ExpressionNode("speciesId", "eq", speciesId);

        if (root == null) {
            root = filterCondition;
        } else {
            root = root.and(filterCondition);
        }

        pageRequest.setConditionTree(root);

        return speciesTaskFertilizerService.scroll(pageRequest);
    }

    @RequestMapping(value = "/{speciesId}/tasks/{speciesTaskId}", method = RequestMethod.GET)
    @ResponseBody
    public ISpeciesTaskFertilizerService.SpeciesTaskForm loadSpeciesTask(@PathVariable Long speciesId, @PathVariable Long speciesTaskId) {
        return speciesTaskFertilizerService.loadForm(speciesTaskId);
    }

    @RequestMapping(value = "/tasks/fertilizers", method = RequestMethod.GET)
    @ResponseBody
    public Collection<SpeciesTaskFertilizerView> fertilizerSpecies(HttpServletRequest request) {
        PageRequest<SpeciesTaskFertilizerView> pageRequest = PageRequestUtils
                .buildPageRequest(SpeciesTaskFertilizerView.class, request.getParameterMap());

        return speciesTaskFertilizerService.scroll(pageRequest);
    }

    @RequestMapping(value = "/{speciesId}/tasks/", method = RequestMethod.POST)
    @ResponseBody
    public Long createSpeciesTask(@PathVariable Long speciesId, @RequestBody ISpeciesTaskFertilizerService.SpeciesTaskForm form, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        form.speciesTask.setOwnerId(userId);
        form.speciesTask.setSpeciesId(speciesId);
        return speciesTaskFertilizerService.saveForm(form);
    }

    @RequestMapping(value = "/{speciesId}/tasks/{speciesTaskId}", method = RequestMethod.PUT)
    @ResponseBody
    public Long updateSpeciesTask(@PathVariable Long speciesId, @PathVariable Long speciesTaskId, @RequestBody ISpeciesTaskFertilizerService.SpeciesTaskForm form) {
        form.speciesTask.setId(speciesTaskId);
        form.speciesTask.setSpeciesId(speciesId);

        return speciesTaskFertilizerService.saveForm(form);
    }

    @RequestMapping(value = "/{speciesId}/tasks/{speciesTaskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteSpeciesTask(@PathVariable Long speciesId, @PathVariable Long speciesTaskId) {
        speciesTaskFertilizerService.deleteSpeciesTask(speciesId, speciesTaskId);
    }
}