package com.i4biz.mygarden.domain.catalog;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "CATALOG")
public class Catalog implements IEntity<Long> {
    public static final long FERTILIZER_GROUP_ID=1;
    public static final long SPECIES_GROUP_ID=2;
    public static final long INVENTORY_GROUP_ID=3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "parent_id", insertable = true, updatable = false)
    Long parentId;

    @Column(name = "owner_id", insertable = true, updatable = false)
    private Long ownerId;

    @Column(name = "name")
    String name;

    public Catalog() {
    }

    public Catalog(Long parentId, Long ownerId, String name) {
        this.parentId = parentId;
        this.ownerId = ownerId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Catalog feedback = (Catalog) o;

        if (id != null ? !id.equals(feedback.id) : feedback.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}