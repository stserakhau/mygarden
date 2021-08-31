package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IItemDAO;
import com.i4biz.mygarden.dao.ISpeciesTaskFertilizerDAO;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import com.i4biz.mygarden.domain.datastorage.EntityTypeEnum;
import com.i4biz.mygarden.domain.user.UserWork;
import com.i4biz.mygarden.service.datastorage.IFileService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class SpeciesService extends AbstractService<ItemView, Item, Long> implements ISpeciesService {
    @Autowired
    private IItemDAO speciesDAO;

    @Autowired
    private IFertilizerService fertilizerService;

    @Autowired
    private ISpeciesCatalogService speciesCatalogService;

    @Autowired
    private IUserWorkService userWorkService;

    @Autowired
    ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Override
    protected GenericDAO<ItemView, Item, Long> getServiceDAO() {
        return speciesDAO;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Collection<Item> findAvailableSpeciesLinkedWithFertilizer(long fertilizerId, Long ownerId) {
        return speciesDAO.findAvailableSpeciesLinkedWithFertilizer(fertilizerId, ownerId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Item createSpecies(SpeciesForm speciesForm) {
        Catalog speciesCatalog = speciesCatalogService.findOrCreate(speciesForm.speciesCatalog.getName(), speciesForm.speciesCatalog.getOwnerId());

        speciesForm.species.setCatalogId(speciesCatalog.getId());
        Item species = speciesForm.species;

        List<Item> searchResult = speciesDAO.findNameExistsInMySet(Catalog.SPECIES_GROUP_ID, species.getName(), species.getOwnerId());
        if (!searchResult.isEmpty()) {
            Item _species = searchResult.get(0);
            if (_species.getId().equals(species.getId())) {
                _species.setName(species.getName());
                _species.setDescription(species.getDescription());
                _species.setCatalogId(speciesCatalog.getId());
                _species.setOptions(species.getOptions());
                species = _species;
            } else {
                throw new ConstraintViolationException("Species " + species.getName() + " : " + species.getOwnerId() + " already exists", null, "business data constraint");
            }
        }

        speciesDAO.saveOrUpdate(species);

        return species;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSpecies(long speciesId, long ownerId) {
        userWorkService.deleteSpeciesPatterns(speciesId, ownerId);
        speciesTaskFertilizerDAO.deleteBySpecies(speciesId, ownerId);
        speciesDAO.deleteByIdOwnerId(speciesId, ownerId);
    }

    @Autowired
    IFileService fileService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long copySystemSpeciesToMy(long systemSpeciesId, ISpeciesService.SpeciesForm speciesForm) {
        Item systemSpecies = speciesDAO.findById(systemSpeciesId, null);
        List<UserWork> systemSpeciesPatterns = userWorkService.allSystemSpeciesPatterns(systemSpeciesId);

        Catalog userSpeciesCatalog = speciesCatalogService.findOrCreate(
                speciesForm.speciesCatalog.getName(),
                speciesForm.speciesCatalog.getOwnerId()
        );

        Item userSpecies = speciesForm.species;
        Item species = speciesDAO.findByName(userSpecies.getName(), userSpecies.getOwnerId());
        if (speciesForm.overwriteSpeciesIfExist) {
            if (species != null) {
                userWorkService.deleteSpeciesPatterns(species.getId(), species.getOwnerId());
                userSpecies = species;
            }
        } else {
            if (species != null) {
                throw new RuntimeException(new ConstraintViolationException("Species exists", null, "java business constraint"));
            }
        }

        userSpecies.setCatalogId(userSpeciesCatalog.getId());
        userSpecies.setDescription(systemSpecies.getDescription());

        try {
            speciesDAO.saveOrUpdate(userSpecies);
            for (UserWork sysWork : systemSpeciesPatterns) {
                userWorkService.copyPatternToUser(
                        sysWork.getId(),
                        userSpecies.getId(),
                        userSpecies.getOwnerId(),
                        true
                );
            }
            fileService.delete(EntityTypeEnum.SPECIES, userSpecies.getId(), userSpecies.getOwnerId());
            fileService.copy(EntityTypeEnum.SPECIES, systemSpecies.getId(), userSpecies.getId(), userSpecies.getOwnerId());
            fertilizerService.copyFertilizersSettings(userSpecies.getId(), userSpecies.getOwnerId());

            return userSpecies.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<Item> findSpeciesUsedInTask(long taskId, Long userId) {
        return speciesDAO.findSpeciesUsedInTask(taskId, userId);
    }
}
