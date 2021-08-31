package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.domain.report.UserWeatherNotificationReport;
import com.i4biz.mygarden.domain.user.UserProfile;

import java.util.List;

public interface IUserProfileDAO extends GenericDAO<UserProfile, UserProfile, Long> {
    List<UserWeatherNotificationReport> findUsersAwaitsWeatherNotification(int pageNum, int pageSize);


}
