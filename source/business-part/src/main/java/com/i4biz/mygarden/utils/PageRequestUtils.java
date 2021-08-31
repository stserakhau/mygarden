package com.i4biz.mygarden.utils;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionTreeBuilder;
import com.i4biz.mygarden.expression.converters.EnumConverter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PageRequestUtils {
    public static final String PARAM_FILTER_QUERY = "query";
    public static final String PARAM_PAGE_SIZE = "pageSize";
    public static final String PARAM_PAGE_NUM = "pageNum";
    public static final String PREFIX_ORDER = "order.";

    public static <T> PageRequest<T> buildPageRequest(Class<T> filterClass,
                                                      Map<String, String[]> parametersMap) {
        int pageNum = 0;
        int pageSize = 20;

        Map<String, String> order = new HashMap<String, String>();

        String filterQuery = null;

        for (Map.Entry<String, String[]> pm : parametersMap.entrySet()) {
            String paramName = pm.getKey();
            String[] paramValue = pm.getValue();

            if (PARAM_PAGE_NUM.equals(paramName)) {
                pageNum = Integer.parseInt(paramValue[0]);
            } else if (PARAM_PAGE_SIZE.equals(paramName)) {
                pageSize = Integer.parseInt(paramValue[0]);
            } else if (PARAM_FILTER_QUERY.equals(paramName)) {
                filterQuery = paramValue[0];
            } else if (paramName.startsWith(PREFIX_ORDER)) {
                String value = paramValue[0];
                order.put(paramName.substring(6), value);
            }
        }
        ExpressionNode root = null;
        if (StringUtils.hasText(filterQuery)) {
            root = ExpressionTreeBuilder.parse(filterQuery);

            applyFilterClassTypes(root, filterClass);
        }

        return new PageRequest<T>(pageNum, pageSize, root, order);
    }

    public static ExpressionNode buildByExpressionString(String filterQuery, Class clazz) {
        if (StringUtils.isEmpty(filterQuery)) {
            return null;
        }
        ExpressionNode root = ExpressionTreeBuilder.parse(filterQuery);

        applyFilterClassTypes(root, clazz);

        return root;
    }

    private static <T> void applyFilterClassTypes(ExpressionNode root, Class<T> filterClass) {
        boolean isBottomOperator = root.left.left == null;
        if (isBottomOperator) {
            String operation = (String) root.value;
            String propertyPath = (String) root.left.value;
            String value = (String) root.right.value;
            if ("custom".equals(operation)) {
                return;
            }

            Class propertyClass = getPropertyClass(propertyPath, filterClass);
            Object val;
            boolean isStringValue = (value.startsWith("'") && value.endsWith("'"))
                    || (value.startsWith("\"") && value.endsWith("\""));
            boolean isArrayValue = (value.startsWith("[") && value.endsWith("]"));

            if (isStringValue) {
                if (propertyClass.isEnum() && ConvertUtils.lookup(propertyClass) == null) {
                    ConvertUtils.register(new EnumConverter(), propertyClass);
                }
                val = ConvertUtils.convert(value.replaceAll("\\'", ""), propertyClass);
            } else if (isArrayValue) {
                Object arrayClass = java.lang.reflect.Array.newInstance(propertyClass, 0);
                if (propertyClass.isEnum() && ConvertUtils.lookup(arrayClass.getClass()) == null) {
                    ConvertUtils.register(new ArrayConverter(arrayClass.getClass(), new EnumConverter(), 10), arrayClass.getClass());
                }
                val = ConvertUtils.convert(value, arrayClass.getClass());

                if ("in".equals(operation) || "nin".equals(operation)) {
                    Object[] values = (Object[]) val;
                    val = Arrays.asList(values);
                } else {
                    val = java.lang.reflect.Array.get(val, 0);
                }
            } else {
                if ("null".equalsIgnoreCase(value)) {
                    val = null;
                } else {
                    val = value;
                }
            }

            root.right.value = val;
        } else {
            applyFilterClassTypes(root.left, filterClass);
            applyFilterClassTypes(root.right, filterClass);
        }
    }

    private static Class getPropertyClass(String propertyName, Class clazz) {
        String[] path = propertyName.split("\\.");
        for (String prop : path) {
            clazz = BeanUtils.findPropertyType(prop, new Class[]{clazz});
        }
        return clazz;
    }
}
