package org.eu.rainx0.raintool.core.starter.data.mybatis.x;

import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractAuditEntity;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractIdEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * xiaoyureed@gmail.com
 * 无法直接注册进 spring, 必须写一个类继承这个类然后注册
 */
public abstract class AbstractBaseServiceX<ID, T extends AbstractIdEntity<ID>>
    extends ServiceImpl<IBaseMapperX<ID, T>, T>
    implements IBaseServiceX<ID, T> {
}
