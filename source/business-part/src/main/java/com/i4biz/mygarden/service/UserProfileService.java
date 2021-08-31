package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IUserDAO;
import com.i4biz.mygarden.dao.IUserProfileDAO;
import com.i4biz.mygarden.domain.region.RegionView;
import com.i4biz.mygarden.domain.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService extends AbstractService<UserProfile, UserProfile, Long> implements IUserProfileService {
    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private IUserProfileDAO userProfileDAO;

    @Autowired
    private IRegionService regionService;

    @Override
    protected GenericDAO<UserProfile, UserProfile, Long> getServiceDAO() {
        return userProfileDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLocation(long userId, String countryName, String cityName) {
        RegionView city = regionService.findOrCreateCity(countryName, cityName);

        UserProfile up = userProfileDAO.findById(userId);
        up.setCityId(city.getId());
        userProfileDAO.update(up);
    }
}
