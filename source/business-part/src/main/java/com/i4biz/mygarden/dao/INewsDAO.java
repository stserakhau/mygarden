package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.domain.news.State;

import java.util.Collection;
import java.util.List;

public interface INewsDAO extends GenericDAO<NewsView, News, Long> {

    Collection<News> findByState(State state);

    List<News> findNewsForNewsletters();
}
