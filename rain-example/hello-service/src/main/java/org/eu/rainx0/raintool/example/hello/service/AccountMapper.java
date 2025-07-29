package org.eu.rainx0.raintool.example.hello.service;

import org.apache.ibatis.annotations.Mapper;
import org.eu.rainx0.raintool.core.starter.data.mybatis.x.IBaseMapperX;
import org.eu.rainx0.raintool.example.hello.service.entity.Account;

/**
 * @author xiaoyu
 * @time 2025/7/27 17:15
 */
@Mapper
public interface AccountMapper extends IBaseMapperX<String, Account> {
}
