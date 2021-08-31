package com.i4biz.mygarden.service;

import com.i4biz.mygarden.domain.user.PatternAuthor;
import com.i4biz.mygarden.domain.user.UserWork;
import com.i4biz.mygarden.domain.user.UserWorkView;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

public interface IUserWorkService extends IService<UserWorkView, UserWork, Long> {
    void deleteUserWork(long userId, long userWorkId);

    UserWorkView findUserWorkViewById(long userWorkId, Long userId);

    void rebuildDates(long userWorkId, Long deleteTask);

    Long cloneUserWorkAsPattern(UserWork userWork);

    Long copyPatternToUser(long userWorkId, long speciesId, long userId, boolean asPattern);

    Long initUserWork(UserWork userWork);

    List<UserWork> allSystemSpeciesPatterns(long systemSpeciesId);

    void deleteSpeciesPatterns(long speciesId, long ownerId);

    Collection<PatternAuthor> getAllAuthors();

    void quickBuildUserWorkFromPattern(long userId, long patternId, Date startDate);
}
