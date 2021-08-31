package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@TypeDef(name = "EnumObject", typeClass = PGEnumUserType.class)
@Table(name = "AUSER")
public class User implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", insertable = true, updatable = false)
    private String email;

    @Column(name = "password", insertable = false, updatable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Type(
            type = "EnumObject",
            parameters = {
                    @Parameter(name = "enumClassName", value = "com.i4biz.mygarden.domain.user.RoleEnum")
            }
    )
    @Column(name = "role", insertable = true, updatable = false)
    private RoleEnum role;

    @Column(name = "registration_confirmed", insertable = true, updatable = false)
    private boolean registrationConfirmed;

    @Column(name = "registration_code", insertable = true, updatable = false)
    private String registrationCode;

    @Column(name = "registration_date", insertable = false, updatable = false)
    private Date registrationDate;

    @Column(name = "last_login_date", insertable = false, updatable = false)
    private Date lastLoginDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

