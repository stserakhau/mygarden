package com.i4biz.mygarden.domain.weather;

import lombok.Data;

import java.util.List;

@Data
public class ModelForecast {
    public City city;
    public int cnt;
    public String cod;
    public Float message;
    public List<DayItem> list;

    @Data
    public static class City {
        public int id;
        public String name;
        public String country;
        public int population;
        public Coord coord;
    }

    @Data
    public static class Coord {
        public float lat;
        public float lon;
    }

    @Data
    public static class DayItem {
        public int clouds;
        public int deg;
        public int dt; //ltimestamp without miliseconds
        public int humidity;
        public float pressure;
        public float speed;
        public float rain;
        public Temp temp;
        public List<Weather> weather;
    }

    @Data
    public static class Temp {
        public float day;
        public float eve;
        public float max;
        public float min;
        public float morn;
        public float night;
    }

    @Data
    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }
}
