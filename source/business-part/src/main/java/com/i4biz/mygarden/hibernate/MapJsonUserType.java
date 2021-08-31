package com.i4biz.mygarden.hibernate;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

public class MapJsonUserType extends AbstractJsonUserType {
    @Override
    public Class returnedClass() {
        return Map.class;
    }

    @Override
    protected TypeReference getTypeReference() {
        return new TypeReference<Map>() {
        };
    }
}