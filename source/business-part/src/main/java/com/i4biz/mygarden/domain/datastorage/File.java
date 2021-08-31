package com.i4biz.mygarden.domain.datastorage;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "FILE")
public class File implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "owner_id", insertable = true, updatable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Type(
            type = "EnumObject",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.datastorage.EntityTypeEnum")
            }
    )
    @Column(name = "associated_entity_type", insertable = true, updatable = false)
    private EntityTypeEnum associatedEntityType;

    @Column(name = "associated_entity_id", length = 50, nullable = true)
    private Long associatedEntityId;


    @Column(name = "mime_type", length = 50, nullable = false)
    private String mimeType;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        File file = (File) o;
        return Objects.equals(id, file.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
