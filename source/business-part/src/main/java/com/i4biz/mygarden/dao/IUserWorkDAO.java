package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.UserWork;
import com.i4biz.mygarden.domain.user.UserWorkView;

import java.util.List;

public interface IUserWorkDAO extends GenericDAO<UserWorkView, UserWork, Long> {
    UserWorkView findUserWorkViewById(long userWorkId, Long userId);

    List<UserWork> findAllSystemSpeciesPatterns(long systemSpeciesId);

    void deleteSpeciesPatterns(long speciesId, long ownerId);

    UserWork findPatternById(long systemWorkId, Long userId);
}
