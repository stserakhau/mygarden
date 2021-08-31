package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.UserWork;
import com.i4biz.mygarden.domain.user.UserWorkView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class UserWorkDAO extends GenericDAOImpl<UserWorkView, UserWork, Long> implements IUserWorkDAO {
    @Override
    public UserWorkView findUserWorkViewById(long userWorkId, Long userId) {
        if(userId==null ){
            return (UserWorkView) getSession()
                    .createQuery("from UserWorkView where id=:id and userId is null")
                    .setLong("id", userWorkId)
                    .uniqueResult();
        } else {
            return (UserWorkView) getSession()
                    .createQuery("from UserWorkView where id=:id and userId=:uid")
                    .setLong("id", userWorkId)
                    .setLong("uid", userId)
                    .uniqueResult();
        }
    }

    @Override
    public List<UserWork> findAllSystemSpeciesPatterns(long systemSpeciesId) {
        return getSession()
                .createQuery("from UserWork where speciesId=:speciesId and userId is null and pattern=true")
                .setLong("speciesId", systemSpeciesId)
                .list();
    }

    @Override
    public void deleteSpeciesPatterns(long speciesId, long ownerId) {
        getSession()
                .createQuery("delete from UserWork where userId=:uid and speciesId=:sid and pattern=true")
                .setLong("uid", ownerId)
                .setLong("sid", speciesId)
                .executeUpdate();
    }

    @Override
    public UserWork findPatternById(long systemWorkId, Long userId) {
        if(userId!=null) {
            return (UserWork) getSession()
                    .createQuery("from UserWork where id=:id and (userId is null or userId=:uid) and pattern=true")
                    .setLong("id", systemWorkId)
                    .setLong("uid", userId)
                    .uniqueResult();
        }

        return (UserWork) getSession()
                .createQuery("from UserWork where id=:id and userId is null and pattern=true")
                .setLong("id", systemWorkId)
                .uniqueResult();
    }
}
