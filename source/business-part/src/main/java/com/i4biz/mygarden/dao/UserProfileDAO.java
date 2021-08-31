package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.report.UserWeatherNotificationReport;
import com.i4biz.mygarden.domain.user.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class UserProfileDAO extends GenericDAOImpl<UserProfile, UserProfile, Long> implements IUserProfileDAO {

    @Override
    public List<UserWeatherNotificationReport> findUsersAwaitsWeatherNotification(int pageNum, int pageSize) {
        int firstResult = pageSize * pageNum;
        return getSession().createSQLQuery(
                "select " +
                        "    up.user_id, " +
                        "    u.email, " +
                        "    up.first_name, " +
                        "    up.last_name, " +
                        "    up.temperature_low_level_notification, " +
                        "    up.temperature_low_level_notification_days_before " +
                        "from user_profile up " +
                        "    inner join weather w on w.net_city_id=up.city_id " +
                        "    inner join auser u on u.id=up.user_id " +
                        "where " +
                        "    up.temperature_low_level_notification is not null" +
                        "    and (up.temperature_low_level_notification_days_before>=1 and w.day1 < up.temperature_low_level_notification" +
                        "      or up.temperature_low_level_notification_days_before>=2 and w.day2 < up.temperature_low_level_notification" +
                        "      or up.temperature_low_level_notification_days_before>=3 and w.day3 < up.temperature_low_level_notification" +
                        "      or up.temperature_low_level_notification_days_before>=4 and w.day4 < up.temperature_low_level_notification" +
                        "      or up.temperature_low_level_notification_days_before>=5 and w.day5 < up.temperature_low_level_notification" +
                        "      or up.temperature_low_level_notification_days_before>=6 and w.day6 < up.temperature_low_level_notification" +
                        "      or up.temperature_low_level_notification_days_before>=7 and w.day7 < up.temperature_low_level_notification" +
                        "    )"
        ).addEntity(UserWeatherNotificationReport.class)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .list();
    }
}
