package com.i4biz.mygarden.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public class OrderByFormula extends Order {

    private static final long serialVersionUID = -802688375409027859L;

    private String sqlFormula;

    protected OrderByFormula(String sqlFormula) {
        super(sqlFormula, false);
        this.sqlFormula = sqlFormula;
    }

    public String toString() {
        return sqlFormula;
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return sqlFormula;
    }

    public static Order sqlFormula(String sqlFormula) {
        return new OrderByFormula(sqlFormula);
    }
}
