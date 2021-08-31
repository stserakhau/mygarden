package com.i4biz.mygarden.dao;


import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.IEntity;

import java.io.Serializable;
import java.util.List;


public interface GenericDAO<VIEWENTITY extends IEntity, CRUDENTITY extends IEntity, ID extends Serializable> {

    /**
     * Method returns all entities
     *
     * @return list of the entities
     * @throws DAOException
     */
    List<VIEWENTITY> findAll(Long ownerId) throws DAOException;

    PageResponse<VIEWENTITY> getPage(PageRequest<VIEWENTITY> pageRequest);

    List<VIEWENTITY> scroll(PageRequest<VIEWENTITY> pageRequest);

    List<CRUDENTITY> scrollE(PageRequest<CRUDENTITY> pageRequest);

    VIEWENTITY getViewById(ID id);

    /**
     * Method returns the entity by specified identifier.
     *
     * @param id identifier
     * @return entity
     * @throws DAOException
     */
    CRUDENTITY findById(ID id) throws DAOException;

    /**
     * Method saves or updates the entity
     *
     * @param entity entity
     * @throws DAOException
     */
    CRUDENTITY saveOrUpdate(CRUDENTITY entity) throws DAOException;

    CRUDENTITY insert(CRUDENTITY entity);

    void update(CRUDENTITY entity) throws DAOException;

    /**
     * Method deletes entity by specified identifier
     *
     * @param entity entity for removing. Identifier field and not-null fields should be populated.
     * @throws DAOException
     */
    void delete(CRUDENTITY entity) throws DAOException;
}
