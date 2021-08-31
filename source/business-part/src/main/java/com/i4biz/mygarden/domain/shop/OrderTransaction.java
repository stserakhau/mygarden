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
@Table(name = "ORDER_TRANSACTION")
@TypeDef(name = "EnumObject", typeClass = PGEnumUserType.class)
public class OrderTransaction implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "order_id")
    Long orderId;

    @Column(name = "date")
    Date date;

    @Enumerated(EnumType.STRING)
    @Type(
            type = "EnumObject",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.shop.Status")
            }
    )
    @Column(name = "from_status")
    Status fromStatus;

    @Enumerated(EnumType.STRING)
    @Type(
            type = "EnumObject",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.shop.Status")
            }
    )
    @Column(name = "to_status")
    Status toStatus;

    @Column(name = "info")
    String info;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderTransaction that = (OrderTransaction) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
