package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.weather.Weather;

import java.util.List;

public interface IWeatherDAO extends GenericDAO<Weather, Weather, Integer> {

    void insertIfNoExist(int cityId, long netCityId);

    void updateWeatherData(int cityId, Float day0, Float day1, Float day2, Float day3, Float day4, Float day5, Float day6);

    List<Number> getUnprocessedCitiesIds(int batchSize);
}
