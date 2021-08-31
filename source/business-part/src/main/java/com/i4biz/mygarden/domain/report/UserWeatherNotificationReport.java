package com.i4biz.mygarden.domain.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
public class UserWeatherNotificationReport implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "temperature_low_level_notification")
    private Integer temperatureLowLevelNotification;

    @Column(name = "temperature_low_level_notification_days_before")
    private Integer temperatureLowLevelNotificationDaysBefore;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserWeatherNotificationReport that = (UserWeatherNotificationReport) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), userId);
    }
}
