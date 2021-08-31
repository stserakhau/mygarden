package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import lombok.Data;

import java.util.Collection;

public interface IItemService extends IService<ItemView, Item, Long> {

    void deleteInventory(long inventoryId, long ownerId);

    Item createItem(ItemForm itemForm);

    void delete(long fertilizerId, long ownerId, boolean deleteClass);

    void resetFertilizerUserSettings(long fertilizerId, long ownerId);

    Collection<Item> findAvailableSpeciesLinkedWithFertilizer(long fertilizerId, Long ownerId);

    @Data
    public static class ItemForm {
        public Catalog catalog;
        public Item item;
        public boolean overwriteIfExist;

        public ItemForm() {
        }

        public ItemForm(Catalog catalog, Item item) {
            this.catalog = catalog;
            this.item = item;
        }
    }
}