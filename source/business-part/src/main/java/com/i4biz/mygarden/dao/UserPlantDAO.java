package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.UserPlant;
import org.springframework.stereotype.Repository;

@Repository()
public class UserPlantDAO extends GenericDAOImpl<UserPlant, UserPlant, Long> implements IUserPlantDAO {

    @Override
    public UserPlant findUserPlantById(long plantId, long userId) {
        return (UserPlant) getSession()
                .createQuery("from UserPlant where id=:id and ownerId=:uid")
                .setLong("id", plantId)
                .setLong("uid", userId)
                .uniqueResult();

    }
}
