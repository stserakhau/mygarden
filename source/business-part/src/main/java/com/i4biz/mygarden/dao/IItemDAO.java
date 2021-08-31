package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;

import java.util.Collection;
import java.util.List;

public interface IItemDAO extends GenericDAO<ItemView, Item, Long> {

    int deleteByIdOwnerId(long itemId, long ownerId);

    void deleteAllChildByMainGroupId(long mainCatalogId, long ownerId);

    List<Item> findByCatalogId(long catalogId);

    Collection<Item> findAvailableSpeciesLinkedWithFertilizer(long fertilizerId, Long ownerId);

    Item findById(long systemSpeciesId, Long ownerId);

    Item findByName(String name, Long ownerId);

    List<Item> findSpeciesUsedInTask(long taskId, Long userId);

    Collection<Item> findAllSorted(long parentId, Long userId);

    Collection<Item> findAllMySorted(long parentId, Long ownerId);

    Collection<Item> findAllSystemSorted(long parentId, Integer month, Long taskId);

    List<Item> findNameExistsInMyAndSuperSet(long catalogId, String name, Long ownerId);

    List<Item> findNameExistsInMySet(long catalogId, String name, Long ownerId);

    void deleteRestrictions(long id);

    void saveRestriction(long fertilizerId1, long fertilizerId2);

    List<Item> findFertilizerRestrictions(long fertilizerId, Long ownerId);

    List<Item> findFertilizerRestrictions(Long[] fertilizerIds, Long ownerId);
}
