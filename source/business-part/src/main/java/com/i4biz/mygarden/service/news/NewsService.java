package com.i4biz.mygarden.service.news;


import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.INewsDAO;
import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.domain.news.State;
import com.i4biz.mygarden.service.AbstractService;
import com.i4biz.mygarden.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipInputStream;

@Service
public class NewsService extends AbstractService<NewsView, News, Long> implements INewsService {
    @Value("${news.deployFolder}")
    String newsDeployFolderPath;

    @Autowired
    INewsDAO newsDAO;

    @Override
    protected GenericDAO<NewsView, News, Long> getServiceDAO() {
        return newsDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processReadyToDeploy() {
        Collection<News> news = newsDAO.findByState(State.READY_TO_DEPLOY);
        for (News n : news) {
            Long id = n.getId();
            unpackNewsContent(id, n.getZipArchive());
            n.setState(State.DEPLOYED);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processReadyToUnDeploy() {
        Collection<News> news = newsDAO.findByState(State.READY_TO_UNDEPLOY);
        for (News n : news) {
            Long id = n.getId();
            deleteNewsContent(id);
            n.setState(State.UNDEPLOYED);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processReadyToDelete() {
        Collection<News> news = newsDAO.findByState(State.READY_TO_DELETE);
        for (News n : news) {
            Long id = n.getId();
            deleteNewsContent(id);
            newsDAO.delete(n);
        }
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<News> findNewsForNewsletters() {
        return newsDAO.findNewsForNewsletters();
    }

    private void unpackNewsContent(Long id, byte[] zipArchive) {
        String destDir = getDeploymentFolderName(id);
        try {
            ZipUtils.unzip(
                    new ZipInputStream(new ByteArrayInputStream(zipArchive)),
                    destDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteNewsContent(Long id) {
        File targetNewsFolder = new File(
                getDeploymentFolderName(id)
        );
        try {
            if (targetNewsFolder.exists()) {
                FileUtils.deleteDirectory(targetNewsFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDeploymentFolderName(Long id) {
        return newsDeployFolderPath + File.separator + id;
    }
}
