package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.UserPlant;

public interface IUserPlantDAO extends GenericDAO<UserPlant, UserPlant, Long> {
    UserPlant findUserPlantById(long plantId, long userId);
}
