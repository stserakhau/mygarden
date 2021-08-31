package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "USER_PROFILE")
public class NotificationSettings implements IEntity<Long> {
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "job_notification_days_before")
    private Integer jobNotificationDaysBefore;

    @Column(name = "temperature_low_level_notification")
    private Integer temperatureLowLevelNotification;

    @Column(name = "temperature_low_level_notification_days_before")
    private Integer temperatureLowLevelNotificationDaysBefore;

    @Column(name = "newsletter")
    private boolean newsletter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationSettings that = (NotificationSettings) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
