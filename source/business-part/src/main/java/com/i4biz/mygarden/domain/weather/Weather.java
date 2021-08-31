package com.i4biz.mygarden.domain.weather;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Date;

@Data
public class Weather implements IEntity<Integer> {
    @Id
    @Column(name = "city_id")
    Integer id;

    @Column(name = "net_city_id")
    long netCityId;

    @Column(name = "updated")
    Date updated;

    @Column(name = "day0")
    Float day0;

    @Column(name = "day1")
    Float day1;

    @Column(name = "day2")
    Float day2;

    @Column(name = "day3")
    Float day3;

    @Column(name = "day4")
    Float day4;

    @Column(name = "day5")
    Float day5;

    @Column(name = "day6")
    Float day6;

    @Column(name = "day7")
    Float day7;
}
