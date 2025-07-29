package org.eu.rainx0.raintool.example.security.simple.entity;

import org.eu.rainx0.raintool.core.starter.data.jpa.x.entity.AbstractIdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 15:17
 */
@Entity
@Data
@Accessors(chain = true)
@DynamicInsert
@DynamicUpdate
public class Account extends AbstractIdEntity {
    private String username;

    private String password;

    private String phone;

    /**
     * jpa 会自动映射到数据库 int 类型
     */
    private Boolean enabled;
}
