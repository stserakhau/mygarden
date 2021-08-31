package com.i4biz.mygarden.service.bypage;


import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ISpeciesInventoryService {
    Collection<Map.Entry<Catalog, List<Item>>> loadSystemSpeciesInventory(Integer month, Long taskId);

    Collection<Map.Entry<Catalog, List<Item>>> loadSpeciesInventory(Long ownerId);

    Item loadMyOrSystemSpeciesById(long specieId, Long userId);
}
