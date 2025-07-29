package org.eu.rainx0.raintool.core.starter.data.jpa.x.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 20:56
 */

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity<By, Time> extends AbstractIdEntity {

    /**
     * Need AuditorAware to support createdBy/updatedBy
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected By createdBy;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    protected Time createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    protected By updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected Time updatedAt;


    // @PreUpdate
    public void fillUpdateInfo() {
    }
}
