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

@Service
public class FertilizerInventoryService implements IFertilizerInventoryService {

    @Autowired
    private ICatalogDAO fertilizerCatalogDAO;

    @Autowired
    private IItemDAO fertilizerDAO;

    @Transactional(propagation = Propagation.NEVER)
    public Collection<Map.Entry<Catalog, List<Item>>> loadFertilizersInventory(Long userId) {
        List<Catalog> catalogItems = fertilizerCatalogDAO.findAvailableByParentIdUserId(Catalog.FERTILIZER_GROUP_ID, userId);

        Map<Long, Catalog> pcMap = new LinkedHashMap<>(catalogItems.size());
        Map<Catalog, List<Item>> map = new LinkedHashMap<>(catalogItems.size());

        for (Catalog pc : catalogItems) {
            map.put(pc, new LinkedList());
            pcMap.put(pc.getId(), pc);
        }

        Collection<Item> fertilizerItems = fertilizerDAO.findAllSorted(Catalog.FERTILIZER_GROUP_ID, userId);

        for (Item p : fertilizerItems) {
            long pcId = p.getCatalogId();
            Catalog pc = pcMap.get(pcId);
            Collection fertilizers = map.get(pc);
            fertilizers.add(p);
        }

        return map.entrySet();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Item loadFertilizerById(long fertilizerId) {
        return fertilizerDAO.findById(fertilizerId);
    }
}
