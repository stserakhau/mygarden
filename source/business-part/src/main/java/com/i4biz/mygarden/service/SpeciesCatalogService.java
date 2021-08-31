package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ICatalogDAO;
import com.i4biz.mygarden.dao.IItemDAO;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeciesCatalogService extends AbstractService<Catalog, Catalog, Long> implements ISpeciesCatalogService {
    @Autowired
    private ICatalogDAO speciesCatalogDAO;

    @Autowired
    private IItemDAO speciesDAO;

    @Override
    protected GenericDAO<Catalog, Catalog, Long> getServiceDAO() {
        return speciesCatalogDAO;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Catalog> getMyCatalogItems(Long ownerId) {
        return speciesCatalogDAO.findByOwnerId(Catalog.SPECIES_GROUP_ID, ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Catalog> getAvailableCatalogItems(long userId) {
        return speciesCatalogDAO.findAll(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCatalog(long catalogId, long ownerId) {
        List<Item> species = speciesDAO.findByCatalogId(catalogId);

        species.forEach(item -> speciesDAO.deleteByIdOwnerId(item.getId(), ownerId));

        speciesCatalogDAO.deleteByIdOwnerId(catalogId, ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCatalogIfEmpty(long speciesCatalogId, long ownerId) {
        List<Item> species = speciesDAO.findByCatalogId(speciesCatalogId);
        if (species.isEmpty()) {
            Catalog sc = speciesCatalogDAO.findById(speciesCatalogId);
            if (sc.getOwnerId() != ownerId) {
                throw new RuntimeException("Can't remove catalog " + speciesCatalogId + " for user " + ownerId);
            }
            speciesCatalogDAO.delete(sc);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Catalog findOrCreate(String name, Long userId) {
        Catalog sc = speciesCatalogDAO.findByName(Catalog.SPECIES_GROUP_ID, name, userId);

        if (sc == null) {
            sc = new Catalog(Catalog.SPECIES_GROUP_ID, userId, name);
            saveOrUpdate(sc);
        }

        return sc;
    }
}
