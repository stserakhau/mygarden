package com.i4biz.mygarden.service.bypage;


import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IFertilizerInventoryService {
    Collection<Map.Entry<Catalog, List<Item>>> loadFertilizersInventory(Long userId);

    Item loadFertilizerById(long fertilizerId);
}
