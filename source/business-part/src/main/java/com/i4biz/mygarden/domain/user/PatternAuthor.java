package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "PATTERN_AUTHOR")
public class PatternAuthor implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "seq")
    private int sequence;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternAuthor userEvent = (PatternAuthor) o;

        return id != null ? id.equals(userEvent.id) : userEvent.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
