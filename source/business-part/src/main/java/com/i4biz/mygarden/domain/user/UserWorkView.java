package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USER_WORK_VIEw")
public class UserWorkView implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_work_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "author")
    private String author;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "species_name")
    private String speciesName;

    @Column(name = "pattern")
    private Boolean pattern;

    @Column(name = "pattern_name")
    private String patternName;

    @Column(name = "pattern_description")
    private String patternDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWorkView userEvent = (UserWorkView) o;

        return id != null ? id.equals(userEvent.id) : userEvent.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
