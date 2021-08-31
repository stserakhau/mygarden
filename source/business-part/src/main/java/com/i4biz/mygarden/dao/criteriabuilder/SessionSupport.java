package com.i4biz.mygarden.dao.criteriabuilder;

import org.hibernate.Session;

public class SessionSupport {
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
