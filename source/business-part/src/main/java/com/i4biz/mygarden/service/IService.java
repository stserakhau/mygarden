package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.IEntity;

import java.io.Serializable;
import java.util.List;

public interface IService<VIEWENTITY extends IEntity, CRUDENTITY extends IEntity, ID extends Serializable> {
    PageResponse<VIEWENTITY> page(PageRequest<VIEWENTITY> pageRequest);

    List<VIEWENTITY> scroll(PageRequest<VIEWENTITY> pageRequest);

    VIEWENTITY findViewById(ID id);

    CRUDENTITY findById(ID id);

    CRUDENTITY saveOrUpdate(CRUDENTITY entity);
}
