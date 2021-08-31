package com.i4biz.mygarden.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "SPECIES_TASK_FERTILIZER")
public class SpeciesTaskFertilizer implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "species_id")
    private long speciesId;

    @Column(name = "task_id")
    private long taskId;

    @Column(name = "fertilizer_id")
    private long fertilizerId;

    @Column(name = "owner_id", insertable = true, updatable = false)
    private Long ownerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpeciesTaskFertilizer that = (SpeciesTaskFertilizer) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
