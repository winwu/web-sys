package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data

public abstract class CommonEntity {
    @CreatedDate
    @Column(name="created_at")
    private Date created_at;

    @LastModifiedDate
    @Column(name="updated_at")
    private Date updated_at;
}
