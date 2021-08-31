package com.i4biz.mygarden.service;

import com.i4biz.mygarden.domain.user.UserProfile;

public interface IUserProfileService extends IService<UserProfile, UserProfile, Long> {

    void updateLocation(long userId, String countryName, String cityName);
}
