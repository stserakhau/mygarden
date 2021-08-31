package com.i4biz.mygarden.web.controller.rest;

import com.i4biz.mygarden.service.mail.IMailSender;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class FeedbackRESTService {
    @Value("${site.email}")
    private String siteEmail;

    @Autowired
    private IMailSender mailSender;

    private static Map<String, String> REFERER_EMAIL_MAP = new HashMap<String, String>() {{
        put("/", "feedback@egardening.ru");
        put("/cooperation.tiles", "cooperation@egardening.ru");
        put("/ads.tiles", "ads@egardening.ru");
    }};

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveFeedback(@RequestBody Feedback feedback, @RequestHeader("referer") String referer) {
        try {
            URL url = new URL(referer);
            String ref = url.getPath();

            String toEmail = REFERER_EMAIL_MAP.get(ref);
            if (toEmail == null) {
                toEmail = "feedback@egardening.ru";
            }


            IMailSender.Email email = new IMailSender.Email(
                    siteEmail,
                    toEmail,
                    feedback.getName(),
                    "Письмо от пользователя <a href='" + feedback.getEmail() + "'>" + feedback.getEmail() + "<br/>"
                            + feedback.getContent()
            );

            mailSender.send(email);
        } catch (Exception e) {
            throw new RuntimeException("feedback.cantsendemail");
        }
    }

    @Data
    public static class Feedback implements Serializable {
        String name;

        String email;

        String content;
    }
}
