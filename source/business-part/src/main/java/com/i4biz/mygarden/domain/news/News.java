package com.i4biz.mygarden.domain.news;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@TypeDef(name = "EnumObject", typeClass = PGEnumUserType.class)
@Entity
@Table(name = "NEWS")
public class News implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Type(
            type = "EnumObject",
            parameters = {
                    @Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.news.Group")
            }
    )
    @Column(name = "news_group")
    Group newsGroup;

    @Column(name = "user_id")
    Long userId;

    @Type(
            type = "EnumObject",
            parameters = {
                    @Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.news.State")
            }
    )
    @Column(name = "state")
    State state;

    @Column(name = "publishing_date")
    Date publishingDate;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "zip_archive")
    byte[] zipArchive;

    @Column(name = "title")
    String title;

    @Column(name = "newsletter_processed")
    boolean newsletterProcessed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News that = (News) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
