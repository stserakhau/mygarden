package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.IEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractService<VIEWENTITY extends IEntity, CRUDENTITY extends IEntity, ID extends Serializable>
        implements IService<VIEWENTITY, CRUDENTITY, ID> {
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageResponse<VIEWENTITY> page(PageRequest<VIEWENTITY> pageRequest) {
        return getServiceDAO().getPage(pageRequest);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<VIEWENTITY> scroll(PageRequest<VIEWENTITY> pageRequest) {
        return getServiceDAO().scroll(pageRequest);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public VIEWENTITY findViewById(ID id) {
        return getServiceDAO().getViewById(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CRUDENTITY findById(ID id) {
        return getServiceDAO().findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CRUDENTITY saveOrUpdate(CRUDENTITY entity) {
        return getServiceDAO().saveOrUpdate(entity);
    }

    protected abstract GenericDAO<VIEWENTITY, CRUDENTITY, ID> getServiceDAO();
}
