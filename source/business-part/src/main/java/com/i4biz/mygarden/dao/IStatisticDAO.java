package com.i4biz.mygarden.dao;

public interface IStatisticDAO {

    void savePerformance(String method, long time);

    void saveReferer(String domain, String referer, String target);
}
