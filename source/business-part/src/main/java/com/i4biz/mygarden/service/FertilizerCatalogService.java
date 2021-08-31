package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ICatalogDAO;
import com.i4biz.mygarden.dao.IItemDAO;
import com.i4biz.mygarden.domain.catalog.Catalog;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FertilizerCatalogService extends AbstractService<Catalog, Catalog, Long> implements IFertilizerCatalogService {
    @Autowired
    private ICatalogDAO catalogDAO;

    @Autowired
    private IItemDAO itemDAO;

    @Override
    protected GenericDAO<Catalog, Catalog, Long> getServiceDAO() {
        return catalogDAO;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Catalog> getCatalogItems(Long ownerId) {
        return catalogDAO.findAll(ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCatalog(long catalogId, long ownerId) {
        try {
            itemDAO.deleteByIdOwnerId(catalogId, ownerId);
            catalogDAO.deleteByIdOwnerId(catalogId, ownerId);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.classContainsFertilizer");
        }
    }
}
