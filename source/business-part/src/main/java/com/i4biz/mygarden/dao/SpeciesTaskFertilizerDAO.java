package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.SpeciesTaskFertilizer;
import com.i4biz.mygarden.domain.SpeciesTaskFertilizerView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class SpeciesTaskFertilizerDAO extends GenericDAOImpl<SpeciesTaskFertilizerView, SpeciesTaskFertilizer, Long> implements ISpeciesTaskFertilizerDAO {

    @Override
    public List<SpeciesTaskFertilizer> findByIds(Long[] speciesTasksIds) {
        return getSession()
                .createQuery("from SpeciesTaskFertilizer where id in (:ids)")
                .setParameterList("ids", speciesTasksIds)
                .list();
    }

    @Override
    public List<SpeciesTaskFertilizer> findBySpeciesTask(long speciesId, long taskId) {
        return (List<SpeciesTaskFertilizer>) getSession()
                .createQuery("from SpeciesTaskFertilizer where speciesId = :sid and taskId = :tid")
                .setLong("sid", speciesId)
                .setLong("tid", taskId)
                .list();
    }

    @Override
    public int deleteByTaskId(long taskId) {
        return getSession()
                .createQuery("delete from SpeciesTaskFertilizer where taskId=:tid")
                .setLong("tid", taskId)
                .executeUpdate();
    }

    @Override
    public void deleteByFertilizerSpecies(long fertilizerId, long speciesTaskId) {
        getSession()
                .createQuery("delete from SpeciesTaskFertilizer where fertilizerId=:fid and id=:stid")
                .setLong("fid", fertilizerId)
                .setLong("stid", speciesTaskId)
                .executeUpdate();
    }

    @Override
    public int deleteByFertilizer(long fertilizerId) {
        return getSession()
                .createQuery("delete from SpeciesTaskFertilizer where fertilizerId=:fid")
                .setLong("fid", fertilizerId)
                .executeUpdate();
    }


    @Override
    public void copySystemToUser(long ownerId) {
        getSession()
                .createSQLQuery(
                        "insert into SPECIES_TASK_FERTILIZER (species_id, task_id, fertilizer_id, owner_id) " +
                                "select species_id, task_id, fertilizer_id, :oid " +
                                "from SPECIES_TASK_FERTILIZER " +
                                "where owner_id is null")
                .setLong("oid", ownerId)
                .executeUpdate();
    }

    @Override
    public void copySystemFertilizerToUser(long fertilizerId, long ownerId) {
        getSession()
                .createSQLQuery(
                        "insert into SPECIES_TASK_FERTILIZER (species_id, task_id, fertilizer_id, owner_id) " +
                                "select species_id, task_id, fertilizer_id, :oid " +
                                "from SPECIES_TASK_FERTILIZER " +
                                "where fertilizer_id=:fid and owner_id is null")
                .setLong("oid", ownerId)
                .setLong("fid", fertilizerId)
                .executeUpdate();
    }

    @Override
    public void deleteByUser(long ownerId) {
        getSession()
                .createQuery("delete from SpeciesTaskFertilizer where ownerId=:oid")
                .setLong("oid", ownerId)
                .executeUpdate();
    }

    @Override
    public void deleteByFertilizerUser(long fertilizerId, long ownerId) {
        getSession()
                .createQuery("delete from SpeciesTaskFertilizer where ownerId=:oid and fertilizerId=:fid")
                .setLong("oid", ownerId)
                .setLong("fid", fertilizerId)
                .executeUpdate();
    }

    @Override
    public void copySystemSpeciesToUser(long speciesId, long ownerId) {
        getSession()
                .createSQLQuery(
                        "insert into SPECIES_TASK_FERTILIZER (species_id, task_id, fertilizer_id, owner_id) " +
                                "select species_id, task_id, fertilizer_id, :oid " +
                                "from SPECIES_TASK_FERTILIZER " +
                                "where species_id=:sid and owner_id is null")
                .setLong("oid", ownerId)
                .setLong("sid", speciesId)
                .executeUpdate();
    }

    @Override
    public void deleteBySpecies(long speciesId, long ownerId) {
        getSession()
                .createQuery("delete from SpeciesTaskFertilizer where ownerId=:oid and speciesId=:sid")
                .setLong("oid", ownerId)
                .setLong("sid", speciesId)
                .executeUpdate();
    }
}