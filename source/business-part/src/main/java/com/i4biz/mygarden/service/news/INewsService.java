package com.i4biz.mygarden.service.news;

import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.service.IService;

import java.util.List;

public interface INewsService extends IService<NewsView, News, Long> {

    void processReadyToDeploy();

    void processReadyToUnDeploy();

    void processReadyToDelete();

    List<News> findNewsForNewsletters();
}
