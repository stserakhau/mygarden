package com.i4biz.mygarden.service.notification;

import com.i4biz.mygarden.dao.IUserDAO;
import com.i4biz.mygarden.dao.IUserProfileDAO;
import com.i4biz.mygarden.dao.IUserTaskDAO;
import com.i4biz.mygarden.dao.IUserWorkDAO;
import com.i4biz.mygarden.domain.catalog.Item;
import com.i4biz.mygarden.domain.report.UserWeatherNotificationReport;
import com.i4biz.mygarden.domain.user.NotificationSettings;
import com.i4biz.mygarden.domain.user.UserProfile;
import com.i4biz.mygarden.domain.user.UserView;
import com.i4biz.mygarden.domain.user.task.UserTaskView;
import com.i4biz.mygarden.service.IUserTaskNotificationService;
import com.i4biz.mygarden.service.documentgenerator.IDocumentGeneratorService;
import com.i4biz.mygarden.service.documentgenerator.xsl.XSLDocumentGeneratorService;
import com.i4biz.mygarden.service.mail.IMailSender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("userTaskNotificationService")
public class UserTaskNotificationService implements IUserTaskNotificationService {
    private Log LOG = LogFactory.getLog(UserTaskNotificationService.class);

    @Autowired
    IUserWorkDAO userWorkDAO;

    @Autowired
    IUserTaskDAO userTaskDAO;

    @Autowired
    IUserDAO userDAO;

    @Autowired
    IUserProfileDAO userProfileDAO;

    @Autowired
    private XSLDocumentGeneratorService xslGenerator;

    @Autowired
    private IMailSender mailSender;

    @Value("${site.email}")
    private String siteEmail;

    @Value("${site.url}")
    private String siteURL;

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendNotification() {
        List<UserTaskView> userTasks = userTaskDAO.findTasksReadyForSendingNotification();

        Map<Long, List<UserTaskView>> userTasksMap = new HashMap<>(userTasks.size() / 3);
        for (UserTaskView userTask : userTasks) {
            Long userId = userTask.getOwnerId();
            List<UserTaskView> tasks = userTasksMap.get(userId);
            if (tasks == null) {
                tasks = new ArrayList<>(3);
                userTasksMap.put(userId, tasks);
            }
            tasks.add(userTask);
        }
        for (Map.Entry<Long, List<UserTaskView>> e : userTasksMap.entrySet()) {
            try {
                String xml = "<tasks>";
                for (UserTaskView utv : e.getValue()) {
                    xml += "<task tid='" + utv.getId() + "' name='" + utv.getTaskName() + "' species='" + utv.getSpeciesName() + "'>";
                    for (Item f : utv.getFertilizers()) {
                        xml += "<fertilizer fid='" + f.getId() + "' name='" + f.getName() + "'/>";
                    }
                    xml += "</task>";
                }
                xml += "</tasks>";

                UserView user = userDAO.getViewById(e.getKey());
                NotificationSettings notificationSetting = userDAO.findNotificationSetting(e.getKey());

                String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                Map<String, Object> params = new HashMap<>();
                params.put("date", today);
                params.put("user", user);
                params.put("notificationSetting", notificationSetting);
                params.put("siteUrl", siteURL);

                byte[] emailContent = xslGenerator.createDocument("task_notification", xml.getBytes(), params);
                mailSender.send(new IMailSender.Email(
                        siteEmail,
                        user.getEmail(),
                        "Список запланированных работ на " + today,
                        new String(emailContent)
                ));
                for (UserTaskView utv : e.getValue()) {
                    userTaskDAO.updateNotificationSend(utv.getId(), true);
                }
            } catch (IDocumentGeneratorService.DocumentGeneratorServiceException ex) {
                for (UserTaskView utv : e.getValue()) {
                    userTaskDAO.updateNotificationSend(utv.getId(), false);
                }
                ex.printStackTrace();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendWeatherNotification() {
        final int pageSize = 500;
        int pageNum = 0;
        List<UserWeatherNotificationReport> userProfiles;
        do {
            userProfiles = userProfileDAO.findUsersAwaitsWeatherNotification(pageNum, pageSize);

            for (UserWeatherNotificationReport reportItem : userProfiles) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date now = new Date();
                    String nowStr = dateFormat.format(now);

                    Date weatherFailDay = new Date(now.getTime() + reportItem.getTemperatureLowLevelNotificationDaysBefore()*24*3600*1000);
                    String weatherFailDayStr = dateFormat.format(weatherFailDay);

                    Map<String, Object> params = new HashMap<>();
                    params.put("today", nowStr);
                    params.put("weatherFailDay", weatherFailDayStr);
                    params.put("reportItem", reportItem);
                    params.put("siteUrl", siteURL);

                    byte[] emailContent = xslGenerator.createDocument("weather_notification", "<root></root>".getBytes(), params);
                    mailSender.send(new IMailSender.Email(
                            siteEmail,
                            reportItem.getEmail(),
                            "Температура опустилась ниже заданного значения",
                            new String(emailContent)
                    ));
                } catch (IDocumentGeneratorService.DocumentGeneratorServiceException ex) {
                    ex.printStackTrace();
                }
            }

            pageNum++;
        } while(userProfiles.size() == pageSize);
    }
}
