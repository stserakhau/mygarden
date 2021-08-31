package com.i4biz.mygarden.dao.pagination;

import com.i4biz.mygarden.expression.ExpressionNode;

import java.util.Map;

public class PageRequest<T> {
    private int pageNum;
    private int pageSize;

    private String filterQuery;

    private ExpressionNode conditionTree;

    private Map<String, String> orderSpecification;

    public PageRequest() {
    }

    public PageRequest(int pageNum, int pageSize, ExpressionNode conditionTree, Map<String, String> orderSpecification) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.conditionTree = conditionTree;
        this.orderSpecification = orderSpecification;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ExpressionNode getConditionTree() {
        return conditionTree;
    }

    public void setConditionTree(ExpressionNode conditionTree) {
        this.conditionTree = conditionTree;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public Map<String, String> getOrderSpecification() {
        return orderSpecification;
    }

    public void setOrderSpecification(Map<String, String> orderSpecification) {
        this.orderSpecification = orderSpecification;
    }
}
