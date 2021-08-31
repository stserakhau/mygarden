package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.security.ApplicationPrincipal;
import com.i4biz.mygarden.service.ICatalogService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/catalog")
public class CatalogRESTService {
    @Autowired
    private ICatalogService catalogService;

    @RequestMapping(value = "/{parentId}/my", method = RequestMethod.GET)
    @ResponseBody
    public List<Catalog> myCatalogList(@PathVariable Long parentId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return catalogService.getMyCatalogItems(parentId, userId);
    }

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    @ResponseBody
    public List<Catalog> availableCatalogList(@PathVariable Long parentId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        return catalogService.getAvailableCatalogItems(parentId, userId);
    }

    @RequestMapping(value = "/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public List<Catalog> catalogList(@PathVariable Long parentId, Principal principal) {
        return availableCatalogList(parentId, principal);//todo remove it
    }

    @RequestMapping(value = "/{catalogId}", method = RequestMethod.GET)
    @ResponseBody
    public Catalog catalog(@PathVariable Long catalogId) {
        return catalogService.findById(catalogId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createCatalog(@RequestBody Catalog catalog, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalog.setOwnerId(userId);
        saveOrUpdate(catalog);
    }

    @RequestMapping(value = "/{catalogId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void saveCatalog(@PathVariable Long catalogId, @RequestBody Catalog catalog, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalog.setId(catalogId);
        saveOrUpdate(catalog);
    }

    private void saveOrUpdate(Catalog catalog) {
        try {
            catalogService.saveOrUpdate(catalog);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.catalog.exists");
        }
    }

    @RequestMapping(value = "/{catalogId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCatalog(@PathVariable Long catalogId, Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalogService.deleteCatalog(catalogId, userId);
    }

    @RequestMapping(value = "/resetUserSettings", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void resetUserSettings(Principal principal) {
        User user = ((ApplicationPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUser();
        Long userId = user.getId();
        catalogService.resetFertilizerUserSettings(userId);
    }

}