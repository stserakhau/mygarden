package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.region.RegionView;

import java.util.List;

public interface IRegionService extends IService<RegionView, RegionView, Long> {
    List<RegionView> getAllCountries();

    List<RegionView> findCitiesByCountry(String countryName);

    RegionView findOrCreateCity(String country, String city);

    Region loadRegionByCityId(long cityId);

    public static class Region {
        public String country;
        public String city;

        public Region(String country, String city) {
            this.country = country;
            this.city = city;
        }
    }
}
