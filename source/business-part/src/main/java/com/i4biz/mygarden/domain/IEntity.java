package com.i4biz.mygarden.domain;

import java.io.Serializable;

public interface IEntity<ID extends Serializable> extends Serializable {
    ID getId();

    void setId(ID id);
}
