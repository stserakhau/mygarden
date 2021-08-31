package com.i4biz.mygarden.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "SPECIES_TASK_FERTILIZER_VIEW")
public class SpeciesTaskFertilizerView implements IEntity<Long> {
    @Id
    @Column(name = "species_task_fertilizer_id")
    public Long id;

    @Column(name = "owner_id")
    public Long ownerId;

    @Column(name = "species_id")
    public Long speciesId;

    @Column(name = "species_name")
    public String speciesName;

    @Column(name = "species_owner_id")
    public Long speciesOwnerId;

    @Column(name = "task_id")
    public Long taskId;

    @Column(name = "task_name")
    public String taskName;

    @Column(name = "task_owner_id")
    public Long taskOwnerId;

    @Column(name = "fertilizer_id")
    public Long fertilizerId;

    @Column(name = "fertilizer_name")
    public String fertilizerName;

    @Column(name = "fertilizer_owner_id")
    public Long fertilizerOwnerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpeciesTaskFertilizerView that = (SpeciesTaskFertilizerView) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
