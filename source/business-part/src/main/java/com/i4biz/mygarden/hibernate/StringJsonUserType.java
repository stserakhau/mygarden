package com.i4biz.mygarden.hibernate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author timfulmer
 */
public class StringJsonUserType extends AbstractJsonUserType {
    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    protected TypeReference getTypeReference() {
        return new TypeReference<String>() {
        };
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        if (rs.getString(names[0]) == null) {
            return null;
        }
        return rs.getString(names[0]);
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }

        st.setObject(index, value, Types.OTHER);
    }
}
