package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.dao.criteriabuilder.CustomPropertyFilterCriteriaBuilder;
import com.i4biz.mygarden.dao.criteriabuilder.SessionSupport;
import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.expression.ExpressionNode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;


public abstract class GenericDAOImpl<VIEWENTITY extends IEntity, CRUDENTITY extends IEntity, ID extends Serializable>
        implements GenericDAO<VIEWENTITY, CRUDENTITY, ID> {

    public Class<VIEWENTITY> viewEntityClass =
            (Class<VIEWENTITY>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];

    public Class<CRUDENTITY> crudClass = (Class<CRUDENTITY>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[1];

    @Autowired
    protected SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<VIEWENTITY> findAll(Long ownerId) throws DAOException {
        try {
            Criteria c = getSession().createCriteria(crudClass);
            if (crudClass.getDeclaredField("ownerId") != null) {
                c.add(Restrictions.or(
                        Restrictions.isNull("ownerId"),
                        Restrictions.eq("ownerId", ownerId)
                ));
            }
            List<VIEWENTITY> veList = c.list();
            return veList;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public PageResponse<VIEWENTITY> getPage(PageRequest<VIEWENTITY> pageRequest) {
        Criteria listCriteria = getSession().createCriteria(viewEntityClass);

        DetachedCriteria rowCountCriteria =
                DetachedCriteria.forClass(viewEntityClass).setProjection(Projections.rowCount());

        Criterion filterCriteria = Utils.buildFilterCriterion(viewEntityClass, pageRequest.getConditionTree(), getCustomPropertyCriteriaBuilder());


        if (filterCriteria != null) {
            rowCountCriteria.add(filterCriteria);
            listCriteria.add(filterCriteria);
        }


        Map<String, String> orderSpecification = pageRequest.getOrderSpecification();
        Utils.appendOrderCriterion(orderSpecification, listCriteria, getCustomPropertyCriteriaBuilder());

        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        if (pageNum >= 0 && pageSize > 0) {
            int firstRow = pageSize * pageNum;
            listCriteria
                    .setFirstResult(firstRow)
                    .setMaxResults(pageSize);
        }

        List<VIEWENTITY> pageItems = listCriteria.list();
        Number count = (Number) rowCountCriteria.getExecutableCriteria(getSession()).uniqueResult();

        return new PageResponse<VIEWENTITY>(pageItems, count.intValue());
    }

    @Override
    public List<VIEWENTITY> scroll(PageRequest<VIEWENTITY> pageRequest) {
        return scroll(viewEntityClass, pageRequest);
    }

    @Override
    public List<CRUDENTITY> scrollE(PageRequest<CRUDENTITY> pageRequest) {
        return scroll(crudClass, pageRequest);
    }


    private <E> List<E> scroll(Class<E> clazz, PageRequest<E> pageRequest) {
        Criteria listCriteria = getSession().createCriteria(clazz);
        Criterion filterCriteria = Utils.buildFilterCriterion(clazz, pageRequest.getConditionTree(), getCustomPropertyCriteriaBuilder());

        if (filterCriteria != null) {
            listCriteria.add(filterCriteria);
        }


        Map<String, String> orderSpecification = pageRequest.getOrderSpecification();
        Utils.appendOrderCriterion(orderSpecification, listCriteria, getCustomPropertyCriteriaBuilder());

        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        if (pageNum >= 0 && pageSize > 0) {
            int firstRow = pageSize * pageNum;
            listCriteria
                    .setFirstResult(firstRow)
                    .setMaxResults(pageSize);
        }

        return listCriteria.list();
    }

    @Override
    public VIEWENTITY getViewById(ID id) {
        return (VIEWENTITY) getSession()
                .createCriteria(viewEntityClass)
                .add(
                        Restrictions.idEq(id)
                )
                .uniqueResult();
    }

    @Override
    public CRUDENTITY findById(ID id) throws DAOException {
        CRUDENTITY ce = (CRUDENTITY) getSession().get(crudClass, id);

        return ce;
    }

    @Override
    public CRUDENTITY saveOrUpdate(CRUDENTITY entity) throws DAOException {
        this.getSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    public CRUDENTITY insert(CRUDENTITY entity) {
        this.getSession().save(entity);
        return entity;
    }

    @Override
    public void update(CRUDENTITY entity) throws DAOException {
        this.getSession().update(entity);
    }

    @Override
    public void delete(CRUDENTITY entity) throws DAOException {
        getSession().delete(entity);
    }


    public Criterion buildFilterCriterion(ExpressionNode filterConditionTree) {
        if (filterConditionTree == null) {
            return null;
        }

        CustomPropertyFilterCriteriaBuilder customPropertyFilterCriteriaBuilder = getCustomPropertyCriteriaBuilder();
        if (customPropertyFilterCriteriaBuilder instanceof SessionSupport) {
            ((SessionSupport) customPropertyFilterCriteriaBuilder).setSession(getSession());
        }

        Criterion criteria = Utils.buildFilterCriterion(viewEntityClass, filterConditionTree, customPropertyFilterCriteriaBuilder);

        return criteria;
    }

    public CustomPropertyFilterCriteriaBuilder getCustomPropertyCriteriaBuilder() {
        return null;
    }
}

