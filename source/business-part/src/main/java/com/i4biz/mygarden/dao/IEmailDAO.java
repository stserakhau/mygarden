package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.email.Email;

public interface IEmailDAO extends GenericDAO<Email, Email, Long> {

    void cleanSentEmails();
}
