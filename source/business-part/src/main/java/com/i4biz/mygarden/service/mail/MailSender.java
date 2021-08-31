package com.i4biz.mygarden.service.mail;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service("mailSender")
public class MailSender implements IMailSender {
    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private IEmailService emailService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void sendEmails() {
        List<com.i4biz.mygarden.domain.email.Email> emails = emailService.scroll(new PageRequest<>(0, 20, new ExpressionNode("send", "isNull", ""), null));

        for(com.i4biz.mygarden.domain.email.Email email : emails) {
            try {
                InputStream is = new ByteArrayInputStream(email.content);
                MimeMessage mimeMessage = javaMailSender.createMimeMessage(is);
                javaMailSender.send(mimeMessage);
                email.setSend(true);
            } catch (MailException e) {
                e.printStackTrace();
                email.setSend(false);
            }
        }
    }

    @Override
    public void send(Email email) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(email.from);
            helper.setTo(email.to);
            helper.setSubject(email.subject);
            helper.setText(email.content, true);

            for (Map.Entry<String, Resource> entry : email.inlines.entrySet()) {
                helper.addInline(entry.getKey(), entry.getValue());
            }

            ByteArrayOutputStream oos = new ByteArrayOutputStream(4096);
            message.writeTo(oos);

            com.i4biz.mygarden.domain.email.Email mail = new com.i4biz.mygarden.domain.email.Email();

            mail.setContent(oos.toByteArray());

            emailService.saveOrUpdate(mail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanSentEmails() {
        emailService.cleanSentEmails();
    }
}
