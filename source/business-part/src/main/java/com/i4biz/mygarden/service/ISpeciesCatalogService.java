package com.i4biz.mygarden.service;

import com.i4biz.mygarden.domain.catalog.Catalog;

import java.util.List;

public interface ISpeciesCatalogService extends IService<Catalog, Catalog, Long> {
    List<Catalog> getMyCatalogItems(Long ownerId);

    List<Catalog> getAvailableCatalogItems(long userId);

    void deleteCatalog(long catalogId, long ownerId);

    void deleteCatalogIfEmpty(long speciesCatalogId, long ownerId);

    Catalog findOrCreate(String name, Long userId);
}
