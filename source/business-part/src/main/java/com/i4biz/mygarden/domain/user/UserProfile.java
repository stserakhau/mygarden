package com.i4biz.mygarden.domain.user;

import com.i4biz.mygarden.domain.IEntity;
import com.i4biz.mygarden.hibernate.PGEnumUserType;
import lombok.Data;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "USER_PROFILE")
public class UserProfile implements IEntity<Long> {
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "user_agreement_accepted", insertable = true, updatable = false)
    public boolean userAgreementAccepted;

    @Column(name = "allow_advertisement")
    public boolean allowAdvertisement;

    @Column(name = "newsletter")
    private boolean newsletter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
