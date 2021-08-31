package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.region.RegionView;

import java.util.List;

public interface IRegionDAO extends GenericDAO<RegionView, RegionView, Long> {

    List<RegionView> getAllCountries();

    List<RegionView> findCitiesByCountry(String countryName);

    RegionView findOrCreateCountry(String countryName);

    RegionView findOrCreateCity(long countryId, String cityName);

    Object[] loadRegionByCityId(long cityId);

    /**
     *
     * @param cityId city id in [regions-network address] map
     * @return [0] - country code; [1] - city name
     */
    Object[] findLocationPathByCityId(long cityId);
}
