package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.catalog.Catalog;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import lombok.Data;

import java.util.List;

public interface IFertilizerService extends IService<ItemView, Item, Long> {

    Item createFertilizer(FertilizerForm fertilizerForm);

    void delete(long fertilizerId, long ownerId, boolean deleteClass);

    void resetFertilizerUserSettings(long fertilizerId, long ownerId);

    void resetFertilizerUserSettings(long ownerId);

    void copyFertilizersSettings(long speciesId, long ownerId);

    void deleteCatalog(long catalogId, long userId);

    List<Item> findFertilizerRestrictions(long fertilizerId, Long userId);

    List<Item> findFertilizerRestrictions(Long[] fertilizerId, Long userId);

    @Data
    public static class FertilizerForm {
        public Catalog catalog;
        public Item item;
        public List<Item> restrictedFertilizers;
    }
}
