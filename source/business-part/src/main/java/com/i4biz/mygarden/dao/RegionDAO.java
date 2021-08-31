package com.i4biz.mygarden.dao;

import com.i4biz.mygarden.domain.region.RegionView;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public class RegionDAO extends GenericDAOImpl<RegionView, RegionView, Long> implements IRegionDAO {
    @Override
    public RegionView saveOrUpdate(RegionView entity) throws DAOException {
        throw new RuntimeException("Deprecated");
    }

    @Override
    public RegionView insert(RegionView entity) {
        throw new RuntimeException("Deprecated");
    }

    @Override
    public void update(RegionView entity) throws DAOException {
        throw new RuntimeException("Deprecated");
    }

    @Override
    public void delete(RegionView entity) throws DAOException {
        throw new RuntimeException("Deprecated");
    }


    @Override
    public List<RegionView> getAllCountries() {
        return getSession()
                .createSQLQuery("select id, name_ru as name, '' as region from net_country order by name_ru")
                .addEntity(RegionView.class)
                .list();
    }

    @Override
    public List<RegionView> findCitiesByCountry(String countryName) {
        return getSession()
                .createSQLQuery("select c.id as id, c.name as name, c.region_name as region " +
                        "from city c " +
                        "inner join net_country nc on nc.id=c.country_id " +
                        "where nc.name_ru = :name")
                .addEntity(RegionView.class)
                .setString("name", countryName)
                .list();
    }

    @Override
    public RegionView findOrCreateCountry(String countryName) {
        RegionView country = (RegionView) getSession()
                .createSQLQuery("select id, name_ru as name, null as region from net_country where name_ru=:name")
                .addEntity(RegionView.class)
                .setString("name", countryName)
                .uniqueResult();

        if (country == null) {
            int countryId = (int) System.currentTimeMillis(); //todo remake to sequence
            getSession()
                    .createSQLQuery("insert into net_country (id, name_ru) values(:id, :name)")
                    .setInteger("id", countryId)
                    .setString("name", countryName)
                    .executeUpdate();
            country = new RegionView((long) countryId, countryName);
        }

        return country;
    }

    @Override
    public RegionView findOrCreateCity(long countryId, String cityName) {
        // table contains data issues with more than one definition of the city
        List<RegionView> cities = getSession()
                .createSQLQuery("select id, name, region_name as region from net_city where country_id=:cid and name=:name order by id")
                .addEntity(RegionView.class)
                .setLong("cid", countryId)
                .setString("name", cityName)
                .list();

        RegionView city = cities.isEmpty() ? null : cities.get(0);

        if (city == null) {
            Number cityId = (Number)getSession()
                    .createSQLQuery("insert into city (country_id, name) values(:cid, :name) returning id")
                    .setLong("cid", countryId)
                    .setString("name", cityName)
                    .list().get(0);
            city = new RegionView(cityId.longValue(), cityName);
        }

        return city;
    }

    @Override
    public Object[] loadRegionByCityId(long cityId) {
        return (Object[]) getSession().createSQLQuery(
                "select nc.name_ru as country, c.name as city from city c " +
                "inner join net_country nc on nc.id=c.country_id " +
                "where c.id=:cid")
                .setLong("cid", cityId)
                .uniqueResult();
    }

    @Override
    public Object[] findLocationPathByCityId(long cityId) {
        return (Object[]) getSession().createSQLQuery(
                "select co.code, ci.name from city ci " +
                "inner join net_country co on ci.country_id=co.id " +
                "where ci.id = :cid")
                .setLong("cid", cityId)
                .uniqueResult();
    }

}
