package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.catalog.Catalog;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class CatalogDAO extends GenericDAOImpl<Catalog, Catalog, Long> implements ICatalogDAO {
    @Override
    public List<Catalog> findAvailableByParentIdUserId(Long parentId, Long userId) {
        Criterion root = Restrictions.isNull("ownerId");
        if (userId != null) {
            root = Restrictions.or(
                    Restrictions.eq("ownerId", userId),
                    root
            );
        }
        return getSession()
                .createCriteria(Catalog.class)
                .add(Restrictions.and(
                        Restrictions.eqOrIsNull("parentId", parentId),
                        root
                ))
                .addOrder(Order.asc("name"))
                .list();
    }

    @Override
    public List<Catalog> findMyByParentIdUserId(Long parentId, long userId) {
        return getSession()
                .createCriteria(Catalog.class)
                .add(Restrictions.and(
                        Restrictions.eqOrIsNull("parentId", parentId),
                        Restrictions.eq("ownerId", userId)
                ))
                .addOrder(Order.asc("name"))
                .list();
    }

    @Override
    public Catalog findByName(Long parentId, String name, Long ownerId) {
        return (Catalog) getSession()
                .createQuery("from Catalog where parentId=:pid and lower(name)=lower(:n) and ownerId=:oid")
                .setLong("pid", parentId)
                .setLong("oid", ownerId)
                .setString("n", name)
                .uniqueResult();
    }

    @Override
    public Catalog findByMyOrSystem(Long parentId, String name, Long ownerId) {
        return (Catalog) getSession()
                .createQuery("from Catalog where parentId=:pid and lower(name)=lower(:n) and (ownerId=:oid or ownerId is null)")
                .setLong("pid", parentId)
                .setLong("oid", ownerId)
                .setString("n", name)
                .uniqueResult();
    }

    @Override
    public void deleteByIdOwnerId(long catalogId, long ownerId) {
        getSession()
                .createQuery("delete from Catalog where id=:id and ownerId=:oid")
                .setLong("id", catalogId)
                .setLong("oid", ownerId)
                .executeUpdate();
    }

    @Override
    public void deleteChildrenByOwnerId(long catalogId, long ownerId) {
        getSession()
                .createQuery("delete from Catalog where parentId=:parentId and ownerId=:oid")
                .setLong("parentId", catalogId)
                .setLong("oid", ownerId)
                .executeUpdate();
    }

    @Override
    public List<Catalog> findByOwnerId(long parentId, Long ownerId) {
        return getSession()
                .createQuery("from Catalog where parentId=:pid and ownerId = :oid")
                .setLong("pid", parentId)
                .setLong("oid", ownerId)
                .list();
    }

    @Override
    public List<Catalog> findAllSystem(long parentId) {
        return getSession()
                .createQuery("from Catalog where parentId=:pid and ownerId is null")
                .setLong("pid", parentId)
                .list();
    }
}
