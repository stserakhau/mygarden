package com.i4biz.mygarden.service;

public interface IWeatherLoaderService {
    void initNewUserProfile(long userProfileId);

    void loadWeather() throws Exception;

}
