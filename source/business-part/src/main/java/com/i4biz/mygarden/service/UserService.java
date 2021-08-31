package com.i4biz.mygarden.service;

import com.i4biz.mygarden.dao.GenericDAO;
import com.i4biz.mygarden.dao.ISpeciesTaskFertilizerDAO;
import com.i4biz.mygarden.dao.IUserDAO;
import com.i4biz.mygarden.dao.IUserProfileDAO;
import com.i4biz.mygarden.domain.region.RegionView;
import com.i4biz.mygarden.domain.user.*;
import com.i4biz.mygarden.service.documentgenerator.IDocumentGeneratorService;
import com.i4biz.mygarden.service.documentgenerator.xsl.XSLDocumentGeneratorService;
import com.i4biz.mygarden.service.mail.IMailSender;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends AbstractService<UserView, User, Long> implements IUserService {
    private static final Log LOG = LogFactory.getLog(UserService.class);
    @Autowired
    private IWeatherLoaderService weatherLoaderService;

    @Autowired
    private IRegionService regionService;

    @Autowired
    private IFertilizerService fertilizerService;

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private IUserProfileDAO userProfileDAO;

    @Autowired
    private XSLDocumentGeneratorService xslGenerator;

    @Autowired
    private IMailSender mailSender;

    @Autowired
    private ISpeciesTaskFertilizerDAO speciesTaskFertilizerDAO;

    @Override
    protected GenericDAO<UserView, User, Long> getServiceDAO() {
        return userDAO;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User findByEmail(String login) {
        return userDAO.findUserByEmail(login);
    }

    @Value("${site.email}")
    private String siteEmail;

    @Value("${site.url}")
    private String siteURL;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User registerUser(RegistrationData rr) {
        User user = rr.user;

        User usr;
        if (user.getId() != null) {
            usr = userDAO.findById(user.getId());
            userDAO.updateEmail(usr.getId(), user.getEmail());
            try {
                BeanUtils.copyProperties(rr.user, user);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            rr.user = usr;
        } else {
            usr = userDAO.findUserByEmail(user.getEmail());

            if (usr != null && usr.getEmail().contains("@")) {
                throw new RuntimeException("user.exists");
            }
        }

        UserProfile userProfile = rr.userProfile;

        String registrationCode = RandomStringUtils.randomAlphabetic(40);
        if (rr.user.getId() == null) {
            rr.user.setRegistrationCode(registrationCode);
            rr.user.setRegistrationConfirmed(false);
        } else {
            userDAO.resetRegistrationPassword(user.getId(), registrationCode);
        }

        this.saveRegistrationData(rr);

        try {
            user.setRegistrationCode(registrationCode);
            Map<String, Object> params = new HashMap<>();
            params.put("user", user);
            params.put("userProfile", userProfile);
            params.put("siteEmail", siteEmail);
            params.put("siteUrl", siteURL);

            byte[] emailContent = xslGenerator.createDocument("registration_email", null, params);
            IMailSender.Email mail = new IMailSender.Email(
                    "info@egardening.ru",
                    user.getEmail(),
                    "Активация профиля пользователя eGardening.ru",
                    new String(emailContent)
            );
            mailSender.send(mail);
        } catch (IDocumentGeneratorService.DocumentGeneratorServiceException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRegistrationData(RegistrationData data) {
        User user = data.user;
        UserProfile userProfile = data.userProfile;

        boolean update = user.getId() != null;

        user = userDAO.saveOrUpdate(user);

        long userId = user.getId();

        if (StringUtils.isEmpty(data.country) || StringUtils.isEmpty(data.city)) {
            userProfile.setCityId(null);
        } else {
            RegionView city = regionService.findOrCreateCity(data.country, data.city);
            Long cityId = city.getId();
            userProfile.setCityId(cityId);
        }
        userProfile.setId(userId);
        if (update) {
            userProfileDAO.update(userProfile);
        } else {
            userProfileDAO.insert(userProfile);
            fertilizerService.resetFertilizerUserSettings(userId);
        }
        weatherLoaderService.initNewUserProfile(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void confirmRegistration(String registrationCode) {
        userDAO.confirmUserRegistration(registrationCode);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetPassword(String registrationCode, String password) {
        User user = userDAO.findUserByRegCode(registrationCode);
        if (user != null) {
            String md5Hash = passwordEncoder.encodePassword(password, null);

            userDAO.resetPassword(user.getId(), md5Hash);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetPassword(long userId, String oldPassword, String newPassword) {
        User u = userDAO.findById(userId);
        if (u == null) {
            throw new RuntimeException("Invalid access for user " + userId);
        }
        String md5Old = passwordEncoder.encodePassword(oldPassword, null);
        String md5Current = u.getPassword();
        String md5New = passwordEncoder.encodePassword(newPassword, null);

        if (!md5Old.equals(md5Current)) {
            LOG.warn("Different hashes - old ["+md5Old+"] not equaled current ["+md5New+"]");
            throw new RuntimeException("userProfile.changePassword.invalid");
        }
        userDAO.resetPassword(userId, md5New);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public RegistrationData loadRegistrationData(long userId) {
        User u = userDAO.findById(userId);
        UserProfile up = userProfileDAO.findById(userId);
        if (up.getCityId() == null) {
            return new RegistrationData(
                    u, up, null, null
            );
        } else {
            IRegionService.Region region = regionService.loadRegionByCityId(up.getCityId());
            return new RegistrationData(
                    u, up, region.country, region.city
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void forgotPassword(String email) {
        User usr = userDAO.findUserByEmail(email);
        if (usr == null) {
            throw new RuntimeException("email.notfound");
        }

        UserProfile userProfile = userProfileDAO.findById(usr.getId());

        String registrationCode = RandomStringUtils.randomAlphabetic(40);
        usr.setRegistrationCode(registrationCode);
        userDAO.resetRegistrationPassword(usr.getId(), registrationCode);

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user", usr);
            params.put("userProfile", userProfile);
            params.put("siteUrl", siteURL);

            byte[] emailContent = xslGenerator.createDocument("forgot_password_email", null, params);
            mailSender.send(new IMailSender.Email(
                    siteEmail,
                    usr.getEmail(),
                    "Восстановление пароля eGardening.ru",
                    new String(emailContent)
            ));
        } catch (IDocumentGeneratorService.DocumentGeneratorServiceException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public NotificationSettings findNotificationSetting(long userId) {
        return userDAO.findNotificationSetting(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateNotificationSettings(NotificationSettings notificationSettings) {
        userDAO.updateNotificationSettings(notificationSettings);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void registerAnonymousUser(User user) {
        String origPwd = user.getPassword();
        user.setRole(RoleEnum.ROLE_USER);
        userDAO.saveOrUpdate(user);
        UserProfile up = new UserProfile();
        up.setId(user.getId());
        up.setUserAgreementAccepted(false);
        up.setAllowAdvertisement(false);
        up.setNewsletter(false);
        userProfileDAO.saveOrUpdate(up);

        long userId = user.getId();

        String md5New = passwordEncoder.encodePassword(origPwd, null);
        userDAO.resetPassword(userId, md5New);

        speciesTaskFertilizerDAO.copySystemToUser(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLoginDate(long id) {
        userDAO.updateLoginDate(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void mergeAnonymousToUser(Long userId, String anonymousUsername) {
        User anonymous = userDAO.findUserByEmail(anonymousUsername);
        long anonymousId = anonymous.getId();
        userDAO.cleanUser(anonymousId);
        userProfileDAO.delete(userProfileDAO.findById(anonymousId));
        userDAO.delete(anonymous);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<UserView> findNewsletterSubscribers() {
        return userDAO.findNewsletterSubscribers();
    }
}
