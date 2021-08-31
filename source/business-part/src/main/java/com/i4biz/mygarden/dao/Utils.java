package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.dao.criteriabuilder.CustomPropertyFilterCriteriaBuilder;
import com.i4biz.mygarden.dao.criteriabuilder.CustomPropertySortCriteriaBuilder;
import com.i4biz.mygarden.expression.ExpressionNode;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Utils {
    public static Criterion buildFilterCriterion(Class clazz, ExpressionNode root, CustomPropertyFilterCriteriaBuilder customPropertyCriteriaBuilder) {
        if (root == null) {
            return null;
        }
        Object value = root.value;
        if (value instanceof String) {
            String operation = (String) value;
            ExpressionNode left = root.left;
            ExpressionNode right = root.right;
            switch (operation) {
                case "and": {
                    return Restrictions.and(
                            buildFilterCriterion(clazz, left, customPropertyCriteriaBuilder),
                            buildFilterCriterion(clazz, right, customPropertyCriteriaBuilder)
                    );
                }
                case "or": {
                    return Restrictions.or(
                            buildFilterCriterion(clazz, left, customPropertyCriteriaBuilder),
                            buildFilterCriterion(clazz, right, customPropertyCriteriaBuilder)
                    );
                }
                case "eq": {
                    Object val = right != null ? right.value : null;
                    val = getCustomTypedValue(clazz, (String) left.value, val);
                    return Restrictions.eqOrIsNull((String) left.value, val);
                }
                case "neq": {
                    Object val = right != null ? right.value : null;
                    val = getCustomTypedValue(clazz, (String) left.value, val);
                    return Restrictions.neOrIsNotNull((String) left.value, val);
                }
                case "in": {
                    Collection values = right != null ? (Collection) right.value : null;
                    values = (Collection) getCustomTypedValueCollection(clazz, (String) left.value, values);
                    return Restrictions.in((String) left.value, values);
                }
                case "nin": {
                    Collection values = right != null ? (Collection) right.value : null;
                    values = (Collection) getCustomTypedValueCollection(clazz, (String) left.value, values);
                    return Restrictions.not(Restrictions.in((String) left.value, values));
                }
                case "gt": {
                    Object val = right != null ? right.value : null;
                    val = getCustomTypedValue(clazz, (String) left.value, val);
                    return Restrictions.gt((String) left.value, val);
                }
                case "lt": {
                    Object val = right != null ? right.value : null;
                    val = getCustomTypedValue(clazz, (String) left.value, val);
                    return Restrictions.lt((String) left.value, val);
                }
                case "ge": {
                    Object val = right != null ? right.value : null;
                    val = getCustomTypedValue(clazz, (String) left.value, val);
                    return Restrictions.ge((String) left.value, val);
                }
                case "le": {
                    Object val = right != null ? right.value : null;
                    val = getCustomTypedValue(clazz, (String) left.value, val);
                    return Restrictions.le((String) left.value, val);
                }
                case "isNull": {
                    return Restrictions.isNull((String) left.value);
                }
                case "isNotNull": {
                    return Restrictions.isNotNull((String) left.value);
                }
                case "contains": {
                    String val = right != null ? (right.value == null ? null : right.value.toString()) : null;
                    boolean useLike = val != null && (val.indexOf('%') > -1 || val.indexOf('*') > -1);
                    if (useLike) {
                        val = val.replace('*', '%');
                        return Restrictions.ilike((String) left.value, val, MatchMode.EXACT);
                    } else {
                        return Restrictions.eqOrIsNull((String) left.value, val);
                    }
                }
                case "nop": {
                    return Restrictions.sqlRestriction("1=1");
                }
                case "custom": {
                    String unquotVal = (String) right.value;
                    if (unquotVal.startsWith("'") || unquotVal.startsWith("\"")) {
                        unquotVal = unquotVal.substring(1);
                    }
                    if (unquotVal.endsWith("'") || unquotVal.endsWith("\"")) {
                        unquotVal = unquotVal.substring(0, unquotVal.length() - 1);
                    }
                    return customPropertyCriteriaBuilder.build((String) left.value, unquotVal);
                }
            }
        }
        throw new RuntimeException("Unknown case: " + root.toString());
    }

    private static Object getCustomTypedValue(Class clazz, String propertyName, Object propertyValue) {
        try {
            String[] path = propertyName.split("\\.");

            for(String pn : path) {
                clazz = clazz.getDeclaredField(pn).getType();
            }
            return ConvertUtils.convert(propertyValue, clazz);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Can not determine type for field " + propertyName + " in class " + clazz.getCanonicalName(), e);
        }
    }

    private static Object getCustomTypedValueCollection(Class clazz, String propertyName, Object propertyValue) {//hot fix of the hotfix
        if (propertyValue instanceof Collection) {
            return propertyValue;
        }

        return getCustomTypedValue(clazz, propertyName, propertyValue);
    }

    public static void appendOrderCriterion(Map<String, String> order, Criteria toCriteria, CustomPropertyFilterCriteriaBuilder customPropertyFilterCriteriaBuilder) {
        if (order != null && !order.isEmpty()) {
            // order index, field name, field value (asc or desc)
            List<Triple<Integer, String, String>> orders = new ArrayList<>();
            for (Map.Entry<String, String> se : order.entrySet()) {
                String asc = se.getValue();
                if (asc == null) {
                    continue;
                }
                if (se.getKey().contains(".")) {
                    // If an order param has an order index
                    String orderIndex = se.getKey().substring(0, se.getKey().indexOf("."));
                    String field = se.getKey().substring(se.getKey().indexOf(".") + 1);
                    if (NumberUtils.isNumber(orderIndex)) {
                        orders.add(new ImmutableTriple<>(Integer.valueOf(orderIndex), field, se.getValue()));
                    } else {
                        // if orderIndex is not a number it means that it is a part of compound field name.
                        orders.add(new ImmutableTriple<>(Integer.MAX_VALUE, se.getKey(), se.getValue()));
                    }
                } else {
                    // If an order param does not have an order index, they are going to the end
                    orders.add(new ImmutableTriple<>(Integer.MAX_VALUE, se.getKey(), se.getValue()));
                }
            }

            orders.sort((o1, o2) -> o1.getLeft().compareTo(o2.getLeft()));

            for (Triple<Integer, String, String> orderParam : orders) {
                String propertyName = orderParam.getMiddle();
                String sortType = orderParam.getRight();

                String orderByPart = null;
                if (customPropertyFilterCriteriaBuilder instanceof CustomPropertySortCriteriaBuilder) {
                    CustomPropertySortCriteriaBuilder sortPartBuilder = (CustomPropertySortCriteriaBuilder) customPropertyFilterCriteriaBuilder;
                    orderByPart = sortPartBuilder.buildCustomOrderBy(propertyName, sortType);
                }

                Order orderClause;
                if (orderByPart == null) {
                    orderClause = "asc".equals(sortType) ? Order.asc(propertyName) : Order.desc(propertyName);
                } else {
                    orderClause = OrderByFormula.sqlFormula(orderByPart);
                }
                toCriteria.addOrder(orderClause);
            }
        }
    }
}
