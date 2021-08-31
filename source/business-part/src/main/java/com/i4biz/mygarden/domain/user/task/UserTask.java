package com.i4biz.mygarden.domain.user.task;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.hibernate.ItemsListJsonUserType;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Entity
@TypeDefs({
        @TypeDef(name = "ItemsListJsonUserType", typeClass = ItemsListJsonUserType.class),
        @TypeDef(name = "status", typeClass = PGEnumUserType.class, parameters = {
                @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.user.task.TaskStatus")
        }),
        @TypeDef(name = "planing_type", typeClass = PGEnumUserType.class, parameters = {
                @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.user.task.TaskPlaningType")
        })
})
@Table(name = "USER_TASK")
public class UserTask implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_work_id")
    private Long userWorkId;

    @Column(name = "status")
    @Type(type = "status")
    private TaskStatus status = TaskStatus.CREATED;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "planing_type")
    @Type(type = "planing_type")
    private TaskPlaningType planingType;

    @Column(name = "avg_temperature")
    private Integer averageTemperature;

    @Column(name = "depends_from_task_id")
    private Long dependsFromTaskId;
    @Column(name = "cnt_days_after_prev_evt")
    private Integer countDaysAfterPrevEvent;

    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "calculated_date")
    private Date calculatedDate;
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "fertilizers")
    @Type(type = "ItemsListJsonUserType")
    private List<Item> fertilizers;

    @Column(name = "notification_ready")
    Boolean notificationReady;

    @Column(name = "notification_sent")
    Boolean notificationSent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTask userTask = (UserTask) o;

        return id != null ? id.equals(userTask.id) : userTask.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
