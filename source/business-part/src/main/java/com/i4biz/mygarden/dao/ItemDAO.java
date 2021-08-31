package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.catalog.ItemView;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository()
public class ItemDAO extends GenericDAOImpl<ItemView, Item, Long> implements IItemDAO {
    @Override
    public int deleteByIdOwnerId(long itemId, long ownerId) {
        return getSession()
                .createQuery("delete from Item where id=:id and ownerId=:oid")
                .setLong("id", itemId)
                .setLong("oid", ownerId)
                .executeUpdate();
    }

    @Override
    public void deleteAllChildByMainGroupId(long mainCatalogId, long ownerId) {
        List<Long> catalogIds = getSession().createQuery("select id from Catalog where parentId=:pid").setLong("pid", mainCatalogId).list();
        if (!catalogIds.isEmpty()) {
            getSession()
                    .createQuery("delete from Item where catalogId in (:catalogIds) and ownerId=:oid")
                    .setParameterList("catalogIds", catalogIds)
                    .setLong("oid", ownerId)
                    .executeUpdate();
        }
    }

    @Override
    public List<Item> findByCatalogId(long catalogId) {
        return getSession()
                .createQuery("from Item where catalogId=:cid")
                .setLong("cid", catalogId)
                .list();
    }

    @Override
    public Collection<Item> findAvailableSpeciesLinkedWithFertilizer(long fertilizerId, Long ownerId) {
        if (ownerId == null) {
            return (Collection<Item>) getSession()
                    .createQuery("select distinct species from Item species " +
                            "inner join SpeciesTaskFertilizer stf on stf.speciesId = species.id " +
                            "where stf.fertilizerId=:fid and (stf.ownerId is null) " +
                            "order by species.name")
                    .setLong("fid", fertilizerId)
                    .list();
        } else {
            return (Collection<Item>) getSession()
                    .createQuery("select distinct species from Item species " +
                            "inner join SpeciesTaskFertilizer stf on stf.speciesId = species.id " +
                            "where stf.fertilizerId=:fid and (stf.ownerId=:oid or stf.ownerId is null) " +
                            "order by species.name")
                    .setLong("fid", fertilizerId)
                    .setLong("oid", ownerId)
                    .list();
        }
    }

    @Override
    public Item findById(long itemId, Long ownerId) {
        return (Item) getSession()
                .createCriteria(Item.class)
                .add(
                        Restrictions.and(
                                Restrictions.eq("id", itemId),
                                Restrictions.eqOrIsNull("ownerId", ownerId)
                        )
                )
                .uniqueResult();
    }

    @Override
    public Item findByName(String name, Long ownerId) {
        return (Item) getSession()
                .createCriteria(Item.class)
                .add(
                        Restrictions.and(
                                Restrictions.eq("name", name),
                                Restrictions.eqOrIsNull("ownerId", ownerId)
                        )
                )
                .uniqueResult();
    }

    @Override
    public List<Item> findSpeciesUsedInTask(long taskId, Long userId) {
        return getSession()
                .createSQLQuery(
                        "SELECT DISTINCT s.* FROM item s " +
                                "  INNER JOIN user_work uw ON uw.species_id=s.id " +
                                "  INNER JOIN user_task ut ON ut.user_work_id=uw.id " +
                                "WHERE ut.task_id=:tid " +
                                "AND (uw.user_id=:uid OR uw.user_id IS NULL) " +
                                "AND (s.owner_id=:uid OR s.owner_id IS NULL) " +
                                "AND (ut.owner_id=:uid OR ut.owner_id IS NULL) " +
                                "ORDER BY s.name")
                .addEntity(Item.class)
                .setLong("tid", taskId)
                .setParameter("uid", userId, LongType.INSTANCE)
                .list();
    }

    @Override
    public Collection<Item> findAllMySorted(long parentId, Long ownerId) {
        Collection<Long> catalogIds = getSession()
                .createQuery("select id from Catalog where parentId=:pid")
                .setLong("pid", parentId)
                .list();

        return getSession()
                .createCriteria(Item.class)
                .add(
                        Restrictions.and(
                                Restrictions.in("catalogId", catalogIds),
                                Restrictions.eqOrIsNull("ownerId", ownerId)
                        )
                )
                .addOrder(Order.asc("catalogId")).addOrder(Order.asc("name"))
                .list();

    }

    @Override
    public Collection<Item> findAllSorted(long parentId, Long ownerId) {
        Collection<Long> catalogIds = getSession()
                .createQuery("select id from Catalog where parentId=:pid")
                .setLong("pid", parentId)
                .list();

        return getSession()
                .createCriteria(Item.class)
                .add(
                        Restrictions.and(
                                Restrictions.in("catalogId", catalogIds),
                                Restrictions.or(
                                        Restrictions.eq("ownerId", ownerId),
                                        Restrictions.isNull("ownerId")
                                )

                        )
                )
                .addOrder(Order.asc("catalogId")).addOrder(Order.asc("name"))
                .list();

    }

    @Override
    public Collection<Item> findAllSystemSorted(long parentId, Integer month, Long taskId) {
        Collection<Long> catalogIds = getSession()
                .createQuery("select id from Catalog where parentId=:pid")
                .setLong("pid", parentId)
                .list();

        if (month != null) {
            Query q = getSession()
                    .createSQLQuery("select distinct sp.* " +
                            "from user_task ut" +
                            "  inner join user_work uw on uw.id=ut.user_work_id" +
                            "  inner join item sp on sp.id=uw.species_id " +
                            "where " +
                            "  sp.catalog_id in (:cids) and sp.owner_id is null " +
                            "  and uw.pattern=true" +
                            "  and uw.user_id is null" +
                            (taskId != null ? "  and ut.task_id=:tid " : "") +
                            "  and :monthNum between extract(month from ut.start_date) and extract(month from ut.end_date) " +
                            "order by sp.catalog_id, sp.name")
                    .addEntity("sp", Item.class)
                    .setParameterList("cids", catalogIds)
                    .setInteger("monthNum", month);
            if (taskId != null) {
                q.setLong("tid", taskId);
            }
            return q.list();
        } else {
            return (Collection<Item>) getSession()
                    .createQuery("from Item where catalogId in (:cids) and ownerId is null order by catalogId, name")
                    .setParameterList("cids", catalogIds)
                    .list();
        }
    }

    @Override
    public List<Item> findNameExistsInMyAndSuperSet(long catalogId, String name, Long ownerId) {
        return getSession()
                .createSQLQuery(
                        "SELECT i.* FROM ITEM i " +
                                "    INNER JOIN CATALOG c ON c.id=i.catalog_id " +
                                "WHERE c.parent_id=:cid AND lower(i.name)=lower(:name) AND (i.owner_id IS NULL OR i.owner_id=:oid) " +
                                "ORDER BY i.owner_id ASC"
                )
                .addEntity("i", Item.class)
                .setLong("cid", catalogId)
                .setLong("oid", ownerId)
                .setString("name", name)
                .list();
    }

    @Override
    public List<Item> findNameExistsInMySet(long catalogId, String name, Long ownerId) {
        return getSession()
                .createSQLQuery(
                        "SELECT i.* FROM ITEM i " +
                                "    INNER JOIN CATALOG c ON c.id=i.catalog_id " +
                                "WHERE c.parent_id=:cid AND lower(i.name)=lower(:name) AND i.owner_id=:oid " +
                                "ORDER BY i.owner_id ASC"
                )
                .addEntity("i", Item.class)
                .setLong("cid", catalogId)
                .setLong("oid", ownerId)
                .setString("name", name)
                .list();
    }

    @Override
    public void deleteRestrictions(long id) {
        getSession()
                .createSQLQuery("DELETE FROM fertilizer_restriction WHERE fertilizer_id1=:id OR fertilizer_id2=:id")
                .setLong("id", id)
                .executeUpdate();
    }

    @Override
    public void saveRestriction(long fertilizerId1, long fertilizerId2) {
        getSession()
                .createSQLQuery("INSERT INTO fertilizer_restriction (fertilizer_id1, fertilizer_id2) VALUES (:id1, :id2)")
                .setLong("id1", fertilizerId1)
                .setLong("id2", fertilizerId2)
                .executeUpdate();
    }

    @Override
    public List<Item> findFertilizerRestrictions(long fertilizerId, Long ownerId) {
        String cond;
        if (ownerId == null) {
            cond = " and i1.owner_id is null ";
        } else {
            cond = " and (i1.owner_id is null or i1.owner_id = :oid) ";
        }
        Query q = getSession()
                .createSQLQuery("select * from (SELECT i1.* " +
                        "FROM fertilizer_restriction fr " +
                        "    INNER JOIN item i1 ON i1.id=fr.fertilizer_id2 " +
                        "WHERE fr.fertilizer_id1=:fid " +
                        cond +
                        "UNION ALL " +
                        "SELECT i1.* " +
                        "FROM fertilizer_restriction fr " +
                        "    INNER JOIN item i1 ON i1.id=fr.fertilizer_id1 " +
                        "WHERE fr.fertilizer_id2=:fid" +
                        cond +
                        ") x order by x.name")
                .addEntity(Item.class)
                .setLong("fid", fertilizerId);
        if (ownerId != null) {
            q.setLong("oid", ownerId);
        }
        return q.list();
    }

    @Override
    public List<Item> findFertilizerRestrictions(Long[] fertilizerIds, Long ownerId) {
        String cond;
        if (ownerId == null) {
            cond = " and i1.owner_id is null ";
        } else {
            cond = " and (i1.owner_id is null or i1.owner_id = :oid) ";
        }
        Query q = getSession()
                .createSQLQuery("SELECT * FROM (SELECT i1.* " +
                        "FROM fertilizer_restriction fr " +
                        "    INNER JOIN item i1 ON i1.id=fr.fertilizer_id2 " +
                        "WHERE fr.fertilizer_id1 IN (:fid)" +
                        cond +
                        "UNION ALL " +
                        "SELECT i1.* " +
                        "FROM fertilizer_restriction fr " +
                        "    INNER JOIN item i1 ON i1.id=fr.fertilizer_id1 " +
                        "WHERE fr.fertilizer_id2 IN (:fid)" +
                        cond +
                        ") x ORDER BY x.name")
                .addEntity(Item.class)
                .setParameterList("fid", fertilizerIds);
        if (ownerId != null) {
            q.setLong("oid", ownerId);
        }
        return q.list();
    }
}