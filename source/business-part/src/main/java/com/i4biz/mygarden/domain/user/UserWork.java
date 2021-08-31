package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USER_WORK")
public class UserWork implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "species_name")
    private String speciesName;

    @Column(name = "pattern")
    private boolean pattern;

    @Column(name = "pattern_name")
    private String patternName;

    @Column(name = "pattern_description")
    private String patternDescription;

    public UserWork() {
    }

    public UserWork(Long userId, Long speciesId) {
        this.userId = userId;
        this.speciesId = speciesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWork userEvent = (UserWork) o;

        return id != null ? id.equals(userEvent.id) : userEvent.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
