package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.catalog.Catalog;

import java.util.List;

public interface IFertilizerCatalogService extends IService<Catalog, Catalog, Long> {
    List<Catalog> getCatalogItems(Long ownerId);

    void deleteCatalog(long catalogId, long ownerId);
}
