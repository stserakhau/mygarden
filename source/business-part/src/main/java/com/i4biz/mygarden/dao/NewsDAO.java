package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.domain.news.State;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository()
public class NewsDAO extends GenericDAOImpl<NewsView, News, Long> implements INewsDAO {
    @Override
    public Collection<News> findByState(State state) {
        return getSession()
                .createQuery("from News where state=:state")
                .setParameter("state", state)
                .list();
    }

    @Override
    public List<News> findNewsForNewsletters() {
        return getSession()
                .createQuery("from News where newsletterProcessed=false")
                .list();
    }
}
