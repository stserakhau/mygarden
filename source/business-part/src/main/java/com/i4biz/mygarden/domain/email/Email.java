package com.i4biz.mygarden.domain.email;

import com.i4biz.mygarden.domain.IEntity;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "EMAIL")
public class Email implements IEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content")
    @Type(type="org.hibernate.type.BinaryType")
    public byte[] content;

    @Column(name = "send")
    Boolean send;
}