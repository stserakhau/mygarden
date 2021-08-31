package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.domain.catalog.Catalog;

import java.util.List;

public interface ICatalogDAO extends GenericDAO<Catalog, Catalog, Long> {

    List<Catalog> findAvailableByParentIdUserId(Long parentId, Long userId);

    List<Catalog> findMyByParentIdUserId(Long parentId, long userId);

    Catalog findByName(Long parentId, String name, Long ownerId);

    Catalog findByMyOrSystem(Long parentId, String name, Long ownerId);

    void deleteByIdOwnerId(long catalogId, long ownerId);

    void deleteChildrenByOwnerId(long catalogId, long ownerId);

    List<Catalog> findByOwnerId(long parentId, Long ownerId);

    List<Catalog> findAllSystem(long parentId);

}
