package com.i4biz.mygarden.service;

import com.i4biz.mygarden.domain.user.UserPlant;

import java.util.Map;

public interface IUserPlantService extends IService<UserPlant, UserPlant, Long> {
    void deleteUserPlant(long userId, long userPlantId);

    UserPlant findUserPlantById(long plantId, long userId);

    void updatePlantModel(Long plantId, Long userId, Map model);
}
