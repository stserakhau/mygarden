package com.i4biz.mygarden.domain.news;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Date;

@Data
@TypeDef(name = "EnumObject", typeClass = PGEnumUserType.class)
@Entity
@Table(name = "NEWS")
public class NewsView implements IEntity<Long> {
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

    @Column(name = "title")
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsView that = (NewsView) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
