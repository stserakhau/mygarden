package com.i4biz.mygarden.service.datastorage;

import com.i4biz.mygarden.domain.datastorage.EntityTypeEnum;
import com.i4biz.mygarden.domain.datastorage.File;
import com.i4biz.mygarden.service.IService;

import java.io.InputStream;
import java.util.List;

public interface IFileService extends IService<File, File, Long> {

    void storeFile(File file, InputStream data);

    void copy(EntityTypeEnum entityType, long srcId, long destId, long ownerId);

    void delete(EntityTypeEnum entityType, long associatedEntityId, long ownerId);

    File findByIdUserId(long fileId, Long ownerId);

    InputStream dataStream(File file);

    List<Object[]> buildHomePageCarousel();
}
