package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IUserPlantDAO;
import com.i4biz.mygarden.domain.user.UserPlant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserPlantService extends AbstractService<UserPlant, UserPlant, Long> implements IUserPlantService {
    @Override
    protected GenericDAO<UserPlant, UserPlant, Long> getServiceDAO() {
        return userPlantDAO;
    }

    @Autowired
    private IUserPlantDAO userPlantDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserPlant(long userId, long userPlantId) {
        UserPlant uw = userPlantDAO.findById(userPlantId);
        if (uw.getOwnerId() != userId) {
            throw new RuntimeException("Invalid access for user " + userId + " to user plant " + userPlantId);
        }

        userPlantDAO.delete(uw);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public UserPlant findUserPlantById(long plantId, long userId) {
        return userPlantDAO.findUserPlantById(plantId, userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePlantModel(Long plantId, Long userId, Map model) {
        UserPlant up = userPlantDAO.findUserPlantById(plantId, userId);
        up.setModel(model);
        userPlantDAO.update(up);
    }
}