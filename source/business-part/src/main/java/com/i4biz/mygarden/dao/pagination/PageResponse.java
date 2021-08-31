package com.i4biz.mygarden.dao.pagination;

import java.util.List;

public class PageResponse<T> {
    private final List<T> items;

    private final int totalCount;

    public PageResponse(List<T> items, int totalCount) {
        this.items = items;
        this.totalCount = totalCount;
    }

    public List<T> getItems() {
        return items;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
