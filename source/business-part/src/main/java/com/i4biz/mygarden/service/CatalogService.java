package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ICatalogDAO;
import com.i4biz.mygarden.dao.IItemDAO;
import com.i4biz.mygarden.dao.ISpeciesTaskFertilizerDAO;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService extends AbstractService<Catalog, Catalog, Long> implements ICatalogService {
    @Autowired
    private ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Autowired
    private ICatalogDAO catalogDAO;

    @Override
    protected GenericDAO<Catalog, Catalog, Long> getServiceDAO() {
        return catalogDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCatalog(long catalogId, Long ownerId) {
        catalogDAO.deleteByIdOwnerId(catalogId, ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Catalog> getAvailableCatalogItems(Long parentId, Long userId) {
        return catalogDAO.findAvailableByParentIdUserId(parentId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Catalog> getMyCatalogItems(Long parentId, long userId) {
        return catalogDAO.findMyByParentIdUserId(parentId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Catalog findOrCreate(Long parentId, String name, Long ownerId) {
        Catalog sc = catalogDAO.findByName(parentId, name, ownerId);

        if (sc == null) {
            sc = new Catalog(parentId, ownerId, name);
            saveOrUpdate(sc);
        }

        return sc;
    }

    @Override
    public void resetFertilizerUserSettings(long userId) {
        speciesTaskFertilizerDAO.deleteByUser(userId);
        speciesTaskFertilizerDAO.copySystemToUser(userId);
    }
}
