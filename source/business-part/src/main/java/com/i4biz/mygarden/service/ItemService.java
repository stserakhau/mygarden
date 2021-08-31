package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ICatalogDAO;
import com.i4biz.mygarden.dao.IItemDAO;
import com.i4biz.mygarden.dao.ISpeciesTaskFertilizerDAO;
import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class ItemService extends AbstractService<ItemView, Item, Long> implements IItemService {
    @Autowired
    private ICatalogService catalogService;

    @Autowired
    private ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Autowired
    private ICatalogDAO catalogDAO;

    @Autowired
    private IItemDAO itemDAO;

    @Override
    protected GenericDAO<ItemView, Item, Long> getServiceDAO() {
        return itemDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteInventory(long inventoryId, long ownerId) {
        Item inv = itemDAO.findById(inventoryId);
        itemDAO.delete(inv);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Item createItem(ItemForm itemForm) {
        Catalog catalog = catalogService.findOrCreate(null, itemForm.catalog.getName(), itemForm.catalog.getOwnerId());

        itemForm.item.setCatalogId(catalog.getId());
        Item item = itemForm.item;

        itemDAO.saveOrUpdate(item);

        return item;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long itemId, long ownerId, boolean deleteClass) {
        {//todo should work only for item from fertilizer catalog
            speciesTaskFertilizerDAO.deleteByFertilizer(itemId);
        }

        Item item = itemDAO.findById(itemId);

        int count = itemDAO.deleteByIdOwnerId(itemId, ownerId);
        if (count != 1) {
            throw new RuntimeException("Invalid itemId=" + itemId + " ownerId=" + ownerId);
        }

        if (deleteClass) {
            List<Item> list = itemDAO.findByCatalogId(item.getCatalogId());
            if (list.isEmpty()) {
                catalogDAO.deleteByIdOwnerId(item.getCatalogId(), ownerId);
            }
        }
    }

    @Override
    public void resetFertilizerUserSettings(long fertilizerId, long ownerId) {
        speciesTaskFertilizerDAO.deleteByFertilizerUser(fertilizerId, ownerId);
        speciesTaskFertilizerDAO.copySystemFertilizerToUser(fertilizerId, ownerId);
    }

    @Override
    public Collection<Item> findAvailableSpeciesLinkedWithFertilizer(long fertilizerId, Long ownerId) {
        return itemDAO.findAvailableSpeciesLinkedWithFertilizer(fertilizerId, ownerId);
    }
}
