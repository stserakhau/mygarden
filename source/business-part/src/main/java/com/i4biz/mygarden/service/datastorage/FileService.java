package com.i4biz.mygarden.service.datastorage;


import com.i4biz.mygarden.dao.DAOException;
import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IFileDAO;
import com.i4biz.mygarden.domain.datastorage.EntityTypeEnum;
import com.i4biz.mygarden.domain.datastorage.File;
import com.i4biz.mygarden.service.AbstractService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;

@Service
public class FileService extends AbstractService<File, File, Long> implements IFileService {

    private static final Log LOG = LogFactory.getLog(FileService.class);

    @Value("${dataStorage.path}")
    java.io.File dataStorageRoot;

    @Autowired
    IFileDAO fileDAO;

    @Override
    protected GenericDAO<File, File, Long> getServiceDAO() {
        return fileDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void storeFile(File file, InputStream data) {
        FileOutputStream fos = null;
        try {
            fileDAO.saveOrUpdate(file);
            String pathInStorage = getStorageFilePath(file);
            java.io.File path = new java.io.File(dataStorageRoot, pathInStorage);
            if (!path.exists()) {
                path.mkdirs();
            }
            fos = new FileOutputStream(new java.io.File(path, String.valueOf(file.getId())));
            IOUtils.copyLarge(data, fos);
            fos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void copy(EntityTypeEnum entityType, long srcId, long destId, long ownerId) {
        try {
            List<File> origList = fileDAO.findByEntityId(srcId, entityType);
            if (origList.isEmpty()) {
                return;
            }
            for (File orig : origList) {
                InputStream dataStream = dataStream(orig);

                File dest = (File) BeanUtils.cloneBean(orig);
                dest.setId(null);
                dest.setOwnerId(ownerId);
                dest.setAssociatedEntityId(destId);
                storeFile(dest, dataStream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(EntityTypeEnum entityType, long associatedEntityId, long ownerId) {
        List<File> files = fileDAO.findByEntityId(associatedEntityId, entityType);
        for (File f : files) {
            try {
                String pathInStorage = getStorageFilePath(f);
                java.io.File path = new java.io.File(dataStorageRoot, pathInStorage);
                java.io.File file = new java.io.File(path, "" + f.getId());
                if(file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                LOG.error("Can't proceed delete operation.", e);
            } finally {
                fileDAO.delete(f);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public File findByIdUserId(long fileId, Long ownerId) {
        return fileDAO.findByIdUserId(fileId, ownerId);
    }

    @Override
    public InputStream dataStream(File file) {
        String pathInStorage = getStorageFilePath(file);
        java.io.File dataFile = new java.io.File(dataStorageRoot, pathInStorage + java.io.File.separator + String.valueOf(file.getId()));

        try {
            return new FileInputStream(dataFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<Object[]> buildHomePageCarousel() {
        return fileDAO.buildHomePageCarousel();
    }


    private static String getStorageFilePath(File file) {
        return file.getAssociatedEntityType().name() + java.io.File.separator
                + file.getAssociatedEntityId();
    }
}
