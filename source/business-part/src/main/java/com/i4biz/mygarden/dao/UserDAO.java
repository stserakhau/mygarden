package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.user.NotificationSettings;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserView;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class UserDAO extends GenericDAOImpl<UserView, User, Long> implements IUserDAO {
    @Override
    public User findUserByEmail(String email) {
        return (User) getSession()
                .createQuery("from User where lower(email)=lower(:email)")
                .setString("email", email)
                .uniqueResult();
    }

    @Override
    public User findUserByRegCode(String registrationCode) {
        return (User) getSession()
                .createQuery("from User where registrationCode=:rc")
                .setString("rc", registrationCode)
                .uniqueResult();
    }

    @Override
    public void confirmUserRegistration(String registrationCode) {
        getSession()
                .createQuery("update User set registrationConfirmed=true where registrationCode=:rc")
                .setString("rc", registrationCode)
                .executeUpdate();
    }

    @Override
    public void resetPassword(long userId, String newPassword) {
        getSession()
                .createQuery("update User set password=:npwd, registrationCode=null where id=:uid")
                .setString("npwd", newPassword)
                .setLong("uid", userId)
                .executeUpdate();
    }

    @Override
    public void resetRegistrationPassword(long userId, String registrationCode) {
        getSession()
                .createQuery("update User set password=null, registrationCode=:rc, registrationConfirmed=false where id=:uid")
                .setString("rc", registrationCode)
                .setLong("uid", userId)
                .executeUpdate();
    }

    @Override
    public NotificationSettings findNotificationSetting(long userId) {
        return getSession().get(NotificationSettings.class, userId);
    }

    @Override
    public void updateNotificationSettings(NotificationSettings notificationSettings) {
        getSession().update(notificationSettings);
    }

    @Override
    public void updateLoginDate(long id) {
        getSession()
                .createQuery("update User set lastLoginDate=now() where id=:uid")
                .setLong("uid", id)
                .executeUpdate();
    }

    @Override
    public void updateEmail(long userId, String email) {
        getSession()
                .createQuery("update User set lower(email)=lower(:email) where id=:id")
                .setLong("id", userId)
                .setString("email", email)
                .executeUpdate();
    }

    @Override
    public void cleanUser(long userId) {
        Session s = getSession();
        s.createQuery("delete from SpeciesTaskFertilizer where ownerId=:oid").setLong("oid", userId).executeUpdate();
        s.createQuery("delete from UserTask where ownerId=:oid").setLong("oid", userId).executeUpdate();
        s.createQuery("delete from UserWork where userId=:oid").setLong("oid", userId).executeUpdate();
        s.createQuery("delete from Item where ownerId=:oid").setLong("oid", userId).executeUpdate();
        s.createQuery("delete from Catalog where ownerId=:oid").setLong("oid", userId).executeUpdate();
        s.createQuery("delete from Task where ownerId=:oid").setLong("oid", userId).executeUpdate();
    }

    @Override
    public List<UserView> findNewsletterSubscribers() {
        return getSession().createQuery("from UserView where newsletter=true").list();
    }
}
