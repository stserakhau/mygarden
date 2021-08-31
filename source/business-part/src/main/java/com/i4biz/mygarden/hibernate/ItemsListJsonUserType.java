package com.i4biz.mygarden.hibernate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.i4biz.mygarden.domain.catalog.Item;

import java.util.List;

/**
 * @author timfulmer
 */
public class ItemsListJsonUserType extends AbstractJsonUserType {
    @Override
    public Class returnedClass() {
        return List.class;
    }

    @Override
    protected TypeReference getTypeReference() {
        return new TypeReference<List<Item>>() {
        };
    }
}