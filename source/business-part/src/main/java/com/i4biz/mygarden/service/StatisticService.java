package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.IStatisticDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticService implements IStatisticService {
    @Autowired
    IStatisticDAO statisticDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void collectReferer(String referer, String target) {
        int pos = referer.indexOf("://");
        pos += 3;
        int last = referer.indexOf("/", pos);
        if (last == -1) {
            last = referer.length();
        }
        String domain = referer.substring(pos, last);

        statisticDAO.saveReferer(domain, referer, target);
    }
}
