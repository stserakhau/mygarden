package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.PatternAuthor;

import java.util.Collection;

public interface IPatternAuthorDAO extends GenericDAO<PatternAuthor, PatternAuthor, Long> {

    Collection<PatternAuthor> findAll();
}
