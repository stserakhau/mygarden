package com.i4biz.mygarden.domain.weather;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Model {
    public String cod;
    public int count;
    public String message;
    public List<City> list;

    @Data
    public static class City {
        public int id;
        public long dt;
        public String name;
        public String base;
        public String cod;
        public Coord coord;

        public Main main;
        public int visibility;
        public Map<String, Object> rain;
        public Map<String, Object> clouds;
        public Wind wind;
        public Sys sys;
        public List<Model.Weather> weather;
    }

    @Data
    public static class Coord {
        public float lat;
        public float lon;
    }

    @Data
    public static class Sys {
        public int id;
        public String country;
        public float message;
        public float sunrise;
        public float sunset;
        public int type;
    }

    @Data
    public static class Wind {
        public float speed;
        public float deg;
    }

    @Data
    public static class Main {
        public float grnd_level;
        public float humidity;
        public float pressure;
        public float sea_level;
        public float temp;
        public float temp_max;
        public float temp_min;
    }

    @Data
    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }
}
