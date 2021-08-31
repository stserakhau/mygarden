package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.catalog.Catalog;

import java.util.List;

public interface ICatalogService extends IService<Catalog, Catalog, Long> {
    void deleteCatalog(long catalogId, Long userId);

    List<Catalog> getAvailableCatalogItems(Long parentId, Long userId);

    List<Catalog> getMyCatalogItems(Long parentId, long userId);

    Catalog findOrCreate(Long parentId, String name, Long ownerId);

    void resetFertilizerUserSettings(long userId);
}