package com.i4biz.mygarden.service.bypage;


import com.i4biz.mygarden.dao.ICatalogDAO;
import com.i4biz.mygarden.dao.IItemDAO;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.*;

@Service
public class SpeciesInventoryService implements ISpeciesInventoryService {

    @Autowired
    private ICatalogDAO speciesCatalogDAO;

    @Autowired
    private IItemDAO itemDAO;

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Collection<Map.Entry<Catalog, List<Item>>> loadSystemSpeciesInventory(Integer month, Long taskId) {
        List<Catalog> catalogItems = speciesCatalogDAO.findAllSystem(Catalog.SPECIES_GROUP_ID);

        Map<Long, Catalog> pcMap = new LinkedHashMap<>(catalogItems.size());
        Map<Catalog, List<Item>> map = new LinkedHashMap<>(catalogItems.size());

        for (Catalog pc : catalogItems) {
            map.put(pc, new LinkedList());
            pcMap.put(pc.getId(), pc);
        }

        Collection<Item> speciesItems = itemDAO.findAllSystemSorted(Catalog.SPECIES_GROUP_ID, month, taskId);

        for (Item p : speciesItems) {
            long pcId = p.getCatalogId();
            Catalog pc = pcMap.get(pcId);
            Collection species = map.get(pc);
            species.add(p);
        }
        return map
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size()>0)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Collection<Map.Entry<Catalog, List<Item>>> loadSpeciesInventory(Long ownerId) {
        List<Catalog> catalogItems = speciesCatalogDAO.findByOwnerId(Catalog.SPECIES_GROUP_ID, ownerId);

        Map<Long, Catalog> pcMap = new LinkedHashMap<>(catalogItems.size());
        Map<Catalog, List<Item>> map = new LinkedHashMap<>(catalogItems.size());

        for (Catalog pc : catalogItems) {
            map.put(pc, new LinkedList());
            pcMap.put(pc.getId(), pc);
        }

        Collection<Item> speciesItems = itemDAO.findAllMySorted(Catalog.SPECIES_GROUP_ID, ownerId);

        for (Item p : speciesItems) {
            long pcId = p.getCatalogId();
            Catalog pc = pcMap.get(pcId);
            Collection species = map.get(pc);
            species.add(p);
        }

        return map.entrySet();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Item loadMyOrSystemSpeciesById(long specieId, Long userId) {
        Item species = itemDAO.findById(specieId, userId);
        if (species == null) {
            species = itemDAO.findById(specieId, null);
        }
        return species;
    }
}
