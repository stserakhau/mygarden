package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.datastorage.EntityTypeEnum;
import com.i4biz.mygarden.domain.datastorage.File;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class FileDAO extends GenericDAOImpl<File, File, Long> implements IFileDAO {

    @Override
    public File findByIdUserId(long fileId, Long ownerId) {
        String hql = ownerId == null ?
                "from File where id=:fid and (ownerId is null)"
                : "from File where id=:fid and (ownerId is null or ownerId=:oid)";
        Query q = getSession()
                .createQuery(hql)
                .setLong("fid", fileId);
        if (ownerId != null) {
            q.setLong("oid", ownerId);
        }
        return (File) q.uniqueResult();
    }

    @Override
    public List<Object[]> buildHomePageCarousel() {
        return getSession().createSQLQuery(
                "select " +
                        "  f.id as fileId," +
                        "  uw.species_id," +
                        "  string_agg(cast(uw.id as text), '|') as patternId, " +
                        "  string_agg(uw.pattern_name, '|') as patternName " +
                        "from file f" +
                        "  inner join user_work uw on uw.species_id=f.associated_entity_id and uw.user_id is null and uw.pattern=true " +
                        "where " +
                        "  f.owner_id is null " +
                        "  and f.associated_entity_type='SPECIES' " +
                        "  and exists(select 1 from user_task where user_work_id=uw.id)" +
                        "group by f.id, uw.species_id"
        ).list();
    }

    @Override
    public List<File> findByEntityId(long entityId, EntityTypeEnum entityType) {
        return getSession()
                .createQuery("from File where associatedEntityId=:aid and associatedEntityType=:aet")
                .setLong("aid", entityId)
                .setParameter("aet", entityType)
                .list();
    }
}
