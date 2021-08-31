package com.i4biz.mygarden.hibernate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.i4biz.mygarden.domain.catalog.options.ItemOptions;

public class ItemOptionsJsonUserType extends AbstractJsonUserType {
    @Override
    public Class returnedClass() {
        return ItemOptions.class;
    }

    @Override
    protected TypeReference getTypeReference() {
        return new TypeReference<ItemOptions>() {
        };
    }
}
