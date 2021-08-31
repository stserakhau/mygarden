package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.weather.Weather;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class WeatherDAO extends GenericDAOImpl<Weather, Weather, Integer> implements IWeatherDAO {
    @Override
    public void insertIfNoExist(int cityId, long netCityId) {
        getSession()
                .createSQLQuery("INSERT INTO weather (city_id, net_city_id) VALUES(:cid, :ncid) ON CONFLICT DO NOTHING")
                .setInteger("cid", cityId)
                .setLong("ncid", netCityId)
                .executeUpdate();
    }

    @Override
    public List<Number> getUnprocessedCitiesIds(int batchSize) {
        return getSession()
                .createSQLQuery("SELECT city_id FROM Weather WHERE updated < date_trunc('day', now())")
                .setMaxResults(batchSize)
                .list();
    }

    @Override
    public void updateWeatherData(int cityId, Float day0, Float day1, Float day2, Float day3, Float day4, Float day5, Float day6) {
        getSession()
                .createSQLQuery("UPDATE weather SET updated = now(), day0=:d0, day1=:d1, day2=:d2, day3=:d3, day4=:d4, day5=:d5, day6=:d6 WHERE city_id=:cid")
                .setParameter("d0", day0)
                .setParameter("d1", day1)
                .setParameter("d2", day2)
                .setParameter("d3", day3)
                .setParameter("d4", day4)
                .setParameter("d5", day5)
                .setParameter("d6", day6)
                .setInteger("cid", cityId)
                .executeUpdate();
    }
}
