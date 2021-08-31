package com.i4biz.mygarden.service.weather;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4biz.mygarden.dao.IRegionDAO;
import com.i4biz.mygarden.dao.IUserProfileDAO;
import com.i4biz.mygarden.dao.IUserTaskDAO;
import com.i4biz.mygarden.dao.IWeatherDAO;
import com.i4biz.mygarden.domain.user.UserProfile;
import com.i4biz.mygarden.domain.weather.Model;
import com.i4biz.mygarden.domain.weather.ModelForecast;
import com.i4biz.mygarden.domain.weather.Weather;
import com.i4biz.mygarden.service.IUserProfileService;
import com.i4biz.mygarden.service.IWeatherLoaderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service("weatherLoader")
public class WeatherLoader implements IWeatherLoaderService {
    private Log LOG = LogFactory.getLog(WeatherLoader.class);

    @Autowired
    IUserProfileService userProfileService;

    @Autowired
    IUserProfileDAO userProfileDAO;

    @Autowired
    IUserTaskDAO userTaskDAO;

    @Autowired
    IWeatherDAO weatherDAO;

    @Autowired
    IRegionDAO regionDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public void initNewUserProfile(long userProfileId) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

        try {
            UserProfile userProfile = userProfileDAO.findById(userProfileId);
            Long netCityId = userProfile.getCityId();
            if(netCityId == null) {
                return;
            }
            Object[] locationPath = regionDAO.findLocationPathByCityId(netCityId);
            if (locationPath == null) {
                throw new RuntimeException("City not found");
            }

            String countryCode = (String) locationPath[0];
            String cityName = (String) locationPath[1];

            String weatherSearchURL = "http://api.openweathermap.org/data/2.5/find?q=" +
                    countryCode + "," + cityName +
                    "&type=like&sort=population&cnt=30&appid=" + weatherAPIKey;

            HttpGet getWheather = new HttpGet(weatherSearchURL);

            String responseBody = httpclient.execute(getWheather, RESPONSE_HANDLER);

            Model m = objectMapper.readValue(responseBody, new TypeReference<Model>() {
            });
            int wetherCityId = m.list.get(0).id;
            weatherDAO.insertIfNoExist(wetherCityId, netCityId);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }
    }
//620181  24811
    /*
    API http://openweathermap.org/api

    http://api.openweathermap.org/data/2.5/find?q=minsk&type=like&sort=population&cnt=30&appid=7e69dc8b3601e3bebbbd897be0427c44

    http://api.openweathermap.org/data/2.5/weather?id=524901&units=metric&appid=7e69dc8b3601e3bebbbd897be0427c44

    http://api.openweathermap.org/data/2.5/forecast/daily?id=524901&units=metric&cnt=8&appid=7e69dc8b3601e3bebbbd897be0427c44
     */

    @Value("${wheather.openweatherApiKey}")
    private String weatherAPIKey;

    @Value("${weather.loader.batchSize}")
    private int batchSize;

    @Value("${weather.loader.enabled}")
    private boolean loaderEnabled;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void loadWeather() throws Exception {
        if (!loaderEnabled) {
            return;
        }
        LOG.debug("Weather loading start.");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        List<Number> citiesIds = weatherDAO.getUnprocessedCitiesIds(batchSize);
        for (Number cityIdVal : citiesIds) {
            int cityId = cityIdVal.intValue();
            String weatherSearchURL = "http://api.openweathermap.org/data/2.5/forecast/daily?id=" + cityId + "&units=metric&appid=" + weatherAPIKey;
            HttpGet getWheather = new HttpGet(weatherSearchURL);
            String jsonData = httpclient.execute(getWheather, RESPONSE_HANDLER);
            saveResult(cityId, jsonData);
        }
    }

    private void saveResult(int cityId, String jsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

        TypeReference<ModelForecast> type = new TypeReference<ModelForecast>() {
        };

        ModelForecast m = objectMapper.readValue(jsonData, type);
        int cnt = m.getCnt();
        Float day0 = cnt < 0 ? null : m.getList().get(0).getTemp().day;
        Float day1 = cnt < 1 ? null : m.getList().get(1).getTemp().day;
        Float day2 = cnt < 2 ? null : m.getList().get(2).getTemp().day;
        Float day3 = cnt < 3 ? null : m.getList().get(3).getTemp().day;
        Float day4 = cnt < 4 ? null : m.getList().get(4).getTemp().day;
        Float day5 = cnt < 5 ? null : m.getList().get(5).getTemp().day;
        Float day6 = cnt < 6 ? null : m.getList().get(6).getTemp().day;

        weatherDAO.updateWeatherData(cityId, day0, day1, day2, day3, day4, day5, day6);

        updateWheatherWorksForCity(cityId);
    }

    private void updateWheatherWorksForCity(int cityId) {
        Weather w = weatherDAO.findById(cityId);
        long profileCityId = w.getNetCityId();

        userTaskDAO.updateWeatherTasksCalculationDate(profileCityId);
    }

    private static final ResponseHandler<String> RESPONSE_HANDLER = new ResponseHandler<String>() {
        @Override
        public String handleResponse(
                final HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }
    };
}
