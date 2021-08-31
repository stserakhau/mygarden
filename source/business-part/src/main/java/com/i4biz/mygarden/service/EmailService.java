package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.IEmailDAO;
import com.i4biz.mygarden.domain.email.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailService extends AbstractService<Email, Email, Long> implements IEmailService {
    @Autowired
    private IEmailDAO emailDAO;

    @Override
    protected GenericDAO<Email, Email, Long> getServiceDAO() {
        return emailDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cleanSentEmails() {
        emailDAO.cleanSentEmails();
    }
}
