package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.user.NotificationSettings;
import com.i4biz.mygarden.domain.user.User;
import com.i4biz.mygarden.domain.user.UserProfile;
import com.i4biz.mygarden.domain.user.UserView;

import java.util.List;

public interface IUserService extends IService<UserView, User, Long> {
    User findByEmail(String login);

    User registerUser(RegistrationData registrationData);

    void saveRegistrationData(RegistrationData data);

    void confirmRegistration(String registrationCode);

    void resetPassword(String registrationCode, String password);

    void resetPassword(long userId, String oldPassword, String newPassword);

    RegistrationData loadRegistrationData(long userId);

    void forgotPassword(String email);

    NotificationSettings findNotificationSetting(long userId);

    void updateNotificationSettings(NotificationSettings notificationSettings);

    void registerAnonymousUser(User user);

    void updateLoginDate(long id);

    void mergeAnonymousToUser(Long userId, String anonymousUsername);

    List<UserView> findNewsletterSubscribers();


    public static class RegistrationData {
        public User user;
        public UserProfile userProfile;

        public String country;
        public String city;

        public RegistrationData() {
        }

        public RegistrationData(User user, UserProfile userProfile, String country, String city) {
            this.user = user;
            this.userProfile = userProfile;
            this.country = country;
            this.city = city;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public UserProfile getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfile userProfile) {
            this.userProfile = userProfile;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

}
