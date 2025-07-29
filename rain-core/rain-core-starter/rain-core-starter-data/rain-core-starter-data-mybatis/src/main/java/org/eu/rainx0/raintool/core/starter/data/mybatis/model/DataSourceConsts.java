package org.eu.rainx0.raintool.core.starter.data.mybatis.model;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 21:16
 */
public interface DataSourceConsts {
    /**
     * 主库，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Master} 注解
     */
    String MASTER = "master";
    /**
     * 从库，推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Slave} 注解
     */
    String SLAVE = "slave";

    /**
     * 在 @{link {@link io.github.xiaoyureed.raincloud.core.starter.database.IdTypeEnvironmentPostProcessor} 中初始化
     */
    DbType DB_TYPE = DbType.H2;
}
