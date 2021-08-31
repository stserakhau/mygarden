package com.i4biz.mygarden.domain.catalog;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.domain.catalog.options.ItemOptions;
import com.i4biz.mygarden.hibernate.ItemOptionsJsonUserType;
import com.i4biz.mygarden.hibernate.ItemsListJsonUserType;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Data
@Entity
@TypeDefs({
        @TypeDef(name = "ItemOptions", typeClass = ItemOptionsJsonUserType.class)
})
@Table(name = "ITEM")
public class Item implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "catalog_id")
    Long catalogId;

    @Column(name = "owner_id", insertable = true, updatable = false)
    Long ownerId;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "options")
    @Type(type = "ItemOptions")
    private ItemOptions options;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item feedback = (Item) o;

        if (id != null ? !id.equals(feedback.id) : feedback.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}