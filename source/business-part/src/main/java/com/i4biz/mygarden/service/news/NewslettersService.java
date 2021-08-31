package com.i4biz.mygarden.service.news;

import com.i4biz.mygarden.domain.email.Email;
import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.domain.user.UserView;
import com.i4biz.mygarden.service.IUserService;
import com.i4biz.mygarden.service.documentgenerator.xsl.XSLDocumentGeneratorService;
import com.i4biz.mygarden.service.mail.IMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("newsletterService")
public class NewslettersService implements INewslettersService {
    @Autowired
    IUserService userService;

    @Autowired
    INewsService newsService;

    @Autowired
    IMailSender mailSender;

    @Autowired
    private XSLDocumentGeneratorService xslGenerator;

    @Override
    public void send() {
        if(true){
            return;
        }
        List<News> newsletters = newsService.findNewsForNewsletters();
        for(News n : newsletters) {
            List<UserView> users = userService.findNewsletterSubscribers();
            //todo need implement from news to newsletter feature
            String content = "";//xslGenerator.createDocument("newsletter", null, null, null);

            IMailSender.Email email = new IMailSender.Email("from@email", "subject", content);
            //<img src="cid:html5logo">
//            email.inlines.put("html5logo", new ClassPathResource("/xsl-templates/registration_email/html5.gif"));

            for (UserView u : users) {
                email.to = u.getEmail();
                mailSender.send(email);
            }
        }
    }
}
