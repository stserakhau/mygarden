package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.domain.user.NotificationSettings;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserView;

import java.util.List;

public interface IUserDAO extends GenericDAO<UserView, User, Long> {
    User findUserByEmail(String login);

    User findUserByRegCode(String registrationCode);

    void confirmUserRegistration(String registrationCode);

    void resetPassword(long userId, String newPassword);

    void resetRegistrationPassword(long userId, String registrationCode);

    NotificationSettings findNotificationSetting(long userId);

    void updateNotificationSettings(NotificationSettings notificationSettings);

    void updateLoginDate(long id);

    void updateEmail(long userId, String email);

    void cleanUser(long userId);

    List<UserView> findNewsletterSubscribers();
}
