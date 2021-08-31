package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.hibernate.MapJsonUserType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "USER_PLANT")
@TypeDef(name = "map_model", typeClass = MapJsonUserType.class)
public class UserPlant implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "name")
    private String name;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "data")
    @Type(type = "map_model")
    private Map model;

    public UserPlant() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPlant userEvent = (UserPlant) o;

        return id != null ? id.equals(userEvent.id) : userEvent.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
