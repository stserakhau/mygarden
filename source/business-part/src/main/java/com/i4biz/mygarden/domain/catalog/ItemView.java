package com.i4biz.mygarden.domain.catalog;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ITEM_VIEW")
public class ItemView implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_owner_id")
    private Long ownerId;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "catalog_name")
    private String catalogName;

    @Column(name = "catalog_owner_id")
    private Long catalogOwnerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemView that = (ItemView) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
