package com.i4biz.mygarden.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository()
public class StatisticDAO implements IStatisticDAO {
    @Autowired
    protected SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void savePerformance(String method, long time) {
        getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("insert into METHOD_STATISTIC (method, execution_time) values(?,?)");
                ps.setString(1, method);
                ps.setLong(2, time);
                ps.executeUpdate();
                ps.close();
            }
        });
    }

    @Override
    public void saveReferer(String domain, String referer, String target) {
        getSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("insert into REFERER_STAT (domain, referer, target) values(?,?,?)");
                ps.setString(1, domain);
                ps.setString(2, referer);
                ps.setString(3, target);
                ps.executeUpdate();
                ps.close();
            }
        });
    }
}
