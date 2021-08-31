package com.i4biz.mygarden.domain.user.task;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.hibernate.ItemsListJsonUserType;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import com.i4biz.mygarden.hibernate.StringJsonUserType;
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
        @TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class),
        @TypeDef(name = "status", typeClass = PGEnumUserType.class, parameters = {
                @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.user.task.TaskStatus")
        }),
        @TypeDef(name = "ItemListJsonUserType", typeClass = ItemsListJsonUserType.class),
})
@Table(name = "USER_TASK_VIEW")
public class UserTaskView implements IEntity<Long> {
    @Id
    @Column(name = "user_task_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Type(type = "status")
    @Column(name = "user_task_status")
    private TaskStatus taskStatus;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "calculated_date")
    private Date calculatedDate;

    @Column(name = "user_work_id")
    private Long userWorkId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "fertilizers")
    @Type(type = "ItemListJsonUserType")
    private List<Item> fertilizers;

    @Column(name = "task_comment")
    private String taskComment;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "pattern")
    private Boolean pattern;

    @Column(name = "pattern_name")
    private String patternName;

    @Column(name = "species_name")
    private String speciesName;

    @Column(name = "notification_ready")
    Boolean notificationReady;

    @Column(name = "notification_sent")
    Boolean notificationSent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserTaskView that = (UserTaskView) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
