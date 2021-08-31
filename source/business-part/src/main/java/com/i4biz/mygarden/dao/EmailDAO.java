package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.email.Email;
import org.springframework.stereotype.Repository;

@Repository()
public class EmailDAO extends GenericDAOImpl<Email, Email, Long> implements IEmailDAO {
    @Override
    public void cleanSentEmails() {
        getSession().createQuery("delete from Email where send=true").executeUpdate();
    }
}
