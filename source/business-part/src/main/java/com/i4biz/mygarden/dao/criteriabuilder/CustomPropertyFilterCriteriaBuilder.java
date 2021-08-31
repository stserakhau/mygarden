package com.i4biz.mygarden.dao.criteriabuilder;

import org.hibernate.criterion.Criterion;

public interface CustomPropertyFilterCriteriaBuilder {
    Criterion build(String propertyName, String propertyValue);
}