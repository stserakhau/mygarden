package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.domain.datastorage.EntityTypeEnum;
import com.i4biz.mygarden.domain.datastorage.File;

import java.util.List;

public interface IFileDAO extends GenericDAO<File, File, Long> {

    File findByIdUserId(long fileId, Long ownerId);

    List<Object[]> buildHomePageCarousel();

    List<File> findByEntityId(long entityId, EntityTypeEnum entityType);
}
