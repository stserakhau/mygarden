package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.*;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import com.i4biz.mygarden.domain.user.task.UserTask;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FertilizerService extends AbstractService<ItemView, Item, Long> implements IFertilizerService {
    @Autowired
    private IItemDAO fertilizerDAO;

    @Autowired
    private ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Autowired
    private ICatalogDAO fertilizerCatalogDAO;

    @Autowired
    private IUserTaskDAO userTaskDAO;

    @Override
    protected GenericDAO<ItemView, Item, Long> getServiceDAO() {
        return fertilizerDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Item createFertilizer(FertilizerForm fertilizerForm) {
        long ownerId = fertilizerForm.item.getOwnerId();
        Catalog fc = fertilizerCatalogDAO.findByMyOrSystem(Catalog.FERTILIZER_GROUP_ID, fertilizerForm.catalog.getName(), ownerId);
        if (fc == null) {
            fc = new Catalog(Catalog.FERTILIZER_GROUP_ID, fertilizerForm.catalog.getOwnerId(), fertilizerForm.catalog.getName());
            fertilizerCatalogDAO.saveOrUpdate(fc);
        }
        fertilizerForm.item.setCatalogId(fc.getId());
        Item f = fertilizerForm.item;
        try {
            List<Item> items = fertilizerDAO.findNameExistsInMyAndSuperSet(Catalog.FERTILIZER_GROUP_ID, f.getName(), ownerId);
            if (!items.isEmpty()) {
                Item _fert = items.get(0);
                if (_fert.getId().equals(f.getId())) {
                    _fert.setName(f.getName());
                    _fert.setDescription(f.getDescription());
                    _fert.setCatalogId(fc.getId());

                    f = _fert;
                } else {
                    throw new RuntimeException("error.fertilizer.exists");
                }
            }
            fertilizerDAO.saveOrUpdate(f);
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw new RuntimeException("error.fertilizer.exists");
        }
        long fertilizerId = f.getId();
        fertilizerDAO.deleteRestrictions(fertilizerId);

        for(Item restriction : fertilizerForm.getRestrictedFertilizers()) {
            fertilizerDAO.saveRestriction(fertilizerId, restriction.getId());
        }

        return f;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long fertilizerId, long ownerId, boolean deleteClass) {
        deleteFertilizerFromUserPatterns(fertilizerId, ownerId);

        speciesTaskFertilizerDAO.deleteByFertilizer(fertilizerId);

        Item f = fertilizerDAO.findById(fertilizerId);

        int count = fertilizerDAO.deleteByIdOwnerId(fertilizerId, ownerId);
        if (count != 1) {
            throw new RuntimeException("Invalid fertilizerId=" + fertilizerId + " ownerId=" + ownerId);
        }

        if (deleteClass) {
            List<Item> list = fertilizerDAO.findByCatalogId(f.getCatalogId());
            if (list.isEmpty()) {
                fertilizerCatalogDAO.deleteByIdOwnerId(f.getCatalogId(), ownerId);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetFertilizerUserSettings(long fertilizerId, long ownerId) {
        speciesTaskFertilizerDAO.deleteByFertilizerUser(fertilizerId, ownerId);
        speciesTaskFertilizerDAO.copySystemFertilizerToUser(fertilizerId, ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetFertilizerUserSettings(long ownerId) {
        speciesTaskFertilizerDAO.deleteByUser(ownerId);
        fertilizerDAO.deleteAllChildByMainGroupId(Catalog.FERTILIZER_GROUP_ID, ownerId);
        fertilizerCatalogDAO.deleteChildrenByOwnerId(Catalog.FERTILIZER_GROUP_ID, ownerId);

        speciesTaskFertilizerDAO.copySystemToUser(ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void copyFertilizersSettings(long speciesId, long ownerId) {
        speciesTaskFertilizerDAO.copySystemSpeciesToUser(speciesId, ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCatalog(long catalogId, long userId) {
        List<Item> fertilizers = fertilizerDAO.findByCatalogId(catalogId);

        fertilizers.forEach(item -> {
            fertilizerDAO.deleteByIdOwnerId(item.getId(), userId);
            deleteFertilizerFromUserPatterns(item.getId(), userId);
        });

        fertilizerCatalogDAO.deleteByIdOwnerId(catalogId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Item> findFertilizerRestrictions(long fertilizerId, Long userId) {
        return fertilizerDAO.findFertilizerRestrictions(fertilizerId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Item> findFertilizerRestrictions(Long[] fertilizerIds, Long userId) {
        return fertilizerDAO.findFertilizerRestrictions(fertilizerIds, userId);
    }

    private void deleteFertilizerFromUserPatterns(long fertilizerId, long ownerId) {
        List<UserTask> patterns = userTaskDAO.findUserPatterns(ownerId);
        for (UserTask pattern : patterns) {
            List<Item> fertilizers = pattern.getFertilizers();
            for (int i = 0; i < fertilizers.size(); i++) {
                Item fertilizer = fertilizers.get(i);
                if (fertilizer.getId().equals(fertilizerId)) {
                    fertilizers.remove(i);
                    userTaskDAO.updateFertilizers(pattern.getId(), fertilizers);
                    break;
                }
            }
        }
    }
}
