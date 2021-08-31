package com.i4biz.mygarden.domain.shop;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "ORDER")
@TypeDef(name = "EnumObject", typeClass = PGEnumUserType.class)
public class Order implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "owner_id", insertable = true, updatable = false)
    Long ownerId;

    @Enumerated(EnumType.STRING)
    @Type(
            type = "EnumObject",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.shop.Status")
            }
    )
    @Column(name = "status", insertable = false, updatable = false)
    Status status = Status.CREATED;

    @Column(name = "created_date", insertable = false, updatable = false)
    Date createdDate;

    @Column(name = "comments")
    String comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id != null ? id.equals(order.id) : order.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
