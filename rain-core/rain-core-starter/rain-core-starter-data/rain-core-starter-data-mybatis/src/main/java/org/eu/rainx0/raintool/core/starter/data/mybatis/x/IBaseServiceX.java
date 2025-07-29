package org.eu.rainx0.raintool.core.starter.data.mybatis.x;

import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractAuditEntity;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractIdEntity;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 09:29
 */
public interface IBaseServiceX<ID, T extends AbstractIdEntity<ID>>
    extends IService<T> {
}
