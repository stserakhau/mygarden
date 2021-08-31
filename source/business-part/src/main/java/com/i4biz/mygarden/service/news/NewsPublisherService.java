package com.i4biz.mygarden.service.news;

import com.i4biz.mygarden.domain.news.Group;
import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.domain.news.State;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service("newsPublisherService")
public class NewsPublisherService implements INewsPublisherService {
    private static final Log LOG = LogFactory.getLog(NewsPublisherService.class);

    @Value("${news.uploadFolder}")
    File newsUploadFolder;

    @Value("${news.uploadedFolder}")
    File newsUploadedFolder;

    @Value("${news.errorFolder}")
    File newsErrorFolder;

    @Autowired
    INewsService newsService;

    @Override
    public void uploadNews() {
        File[] newsPackages = newsUploadFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".zip");
            }
        });

        for (File file : newsPackages) {
            try {
                News news = createNewsFromZip(file);

                newsService.saveOrUpdate(news);
                String fileName = file.getName() + "." + System.currentTimeMillis();
                FileUtils.moveFile(file, new File(newsUploadedFolder, fileName));
            } catch (Exception e) {
                String errorFileName = file.getName() + "." + System.currentTimeMillis() + ".error";
                try {
                    FileUtils.moveFile(file, new File(newsErrorFolder, errorFileName));
                    File errorFile = new File(newsErrorFolder, errorFileName);
                    PrintWriter fw = new PrintWriter(new FileWriter(errorFile));
                    e.printStackTrace(fw);
                    fw.close();
                } catch (IOException e1) {
                    LOG.error("Can't write error file: " + errorFileName, e1);
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void processDeployments() {
        newsService.processReadyToDeploy();
        newsService.processReadyToUnDeploy();
        newsService.processReadyToDelete();
    }

    private static News createNewsFromZip(File file) throws Exception {
        News news = new News();

        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String fileName = entry.getName();
            if ("deployment.properties".equals(fileName)) {
                InputStream stream = zipFile.getInputStream(entry);
                Properties props = new Properties();
                props.load(stream);

                news.setTitle(new String(props.getProperty("title").getBytes("ISO-8859-1")));
                news.setNewsGroup(Group.valueOf(props.getProperty("newsGroup")));
                news.setPublishingDate(SDF.parse(props.getProperty("publishingDate")));
                break;
            }
        }
        zipFile.close();

        byte[] binaryContent = FileUtils.readFileToByteArray(file);
        news.setZipArchive(binaryContent);
        news.setState(State.READY_TO_DEPLOY);

        return news;
    }
}
