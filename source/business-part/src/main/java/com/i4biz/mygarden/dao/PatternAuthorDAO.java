package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.PatternAuthor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository()
public class PatternAuthorDAO extends GenericDAOImpl<PatternAuthor, PatternAuthor, Long> implements IPatternAuthorDAO {
    @Override
    public Collection<PatternAuthor> findAll() {
        return getSession()
                .createQuery("from PatternAuthor order by seq")
                .list();
    }
}
