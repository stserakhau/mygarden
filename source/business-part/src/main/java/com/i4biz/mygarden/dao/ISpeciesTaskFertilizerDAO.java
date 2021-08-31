package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.SpeciesTaskFertilizer;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizerView;

import java.util.List;

public interface ISpeciesTaskFertilizerDAO extends GenericDAO<SpeciesTaskFertilizerView, SpeciesTaskFertilizer, Long> {
    List<SpeciesTaskFertilizer> findByIds(Long[] speciesTasksIds);

    List<SpeciesTaskFertilizer> findBySpeciesTask(long speciesId, long taskId);

    int deleteByTaskId(long taskId);

    void deleteByFertilizerSpecies(long fertilizerId, long speciesTaskId);

    int deleteByFertilizer(long fertilizerId);

    void copySystemToUser(long userId);

    void copySystemFertilizerToUser(long fertilizerId, long userId);

    void deleteByUser(long userId);

    void deleteByFertilizerUser(long fertilizerId, long userId);

    void copySystemSpeciesToUser(long speciesId, long ownerId);

    void deleteBySpecies(long speciesId, long ownerId);
}
