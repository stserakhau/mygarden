package com.i4biz.mygarden.service;


import com.i4biz.mygarden.domain.email.Email;

public interface IEmailService extends IService<Email, Email, Long> {

    void cleanSentEmails();
}
