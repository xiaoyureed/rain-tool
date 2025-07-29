package org.eu.rainx0.raintool.example.hello.service.entity;

import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractAuditEntity;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractIdEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 15:17
 */
@Data
@Accessors(chain = true)
// @TableName("account")
@TableName
public class Account extends AbstractAuditEntity<String> {
    private String username;

    private String password;

    private String phone;

    private Boolean enabled;
}
