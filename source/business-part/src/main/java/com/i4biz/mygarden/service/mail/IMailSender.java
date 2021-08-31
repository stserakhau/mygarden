package com.i4biz.mygarden.service.mail;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

public interface IMailSender {
    void sendEmails();

    void send(Email email);

    void cleanSentEmails();

    @Data
    public static class Email {
        public String from;
        public String to;
        public String subject;
        public String content;
        public Map<String, Resource> inlines = new HashMap<>(2);

        public Email(String from, String subject, String content) {
            this(from, null, subject, content);
        }

        public Email(String from, String to, String subject, String content) {
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.content = content;
        }
    }
}
