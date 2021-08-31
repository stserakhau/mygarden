package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IRegionDAO;
import com.i4biz.mygarden.domain.region.RegionView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegionService extends AbstractService<RegionView, RegionView, Long> implements IRegionService {
    @Autowired
    private IRegionDAO regionDAO;

    @Override
    protected GenericDAO<RegionView, RegionView, Long> getServiceDAO() {
        return regionDAO;
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<RegionView> getAllCountries() {
        return regionDAO.getAllCountries();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<RegionView> findCitiesByCountry(String countryName) {
        return regionDAO.findCitiesByCountry(countryName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RegionView findOrCreateCity(String countryName, String cityName) {
        RegionView country = regionDAO.findOrCreateCountry(countryName);
        RegionView city = regionDAO.findOrCreateCity(country.getId(), cityName);
        return city;
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Region loadRegionByCityId(long cityId) {
        Object[] data = regionDAO.loadRegionByCityId(cityId);
        return new Region((String) data[0], (String) data[1]);
    }
}
