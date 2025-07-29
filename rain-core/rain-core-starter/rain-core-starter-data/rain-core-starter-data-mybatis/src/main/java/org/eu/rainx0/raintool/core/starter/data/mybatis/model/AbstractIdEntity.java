package org.eu.rainx0.raintool.core.starter.data.mybatis.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiaoyu
 * @time 2025/7/27 17:04
 */
@Getter
@Setter
public abstract class AbstractIdEntity<ID>
    //    extends Model<T>
    implements Serializable {

    /**
     * AUTO 数据库 ID自增
     * NONE 未设置主键类型，也就是跟随全局策略，全局策略默认为ASSIGN_ID
     * INPUT 在insert 前人工自行 set 主键值
     * ASSIGN_ID 框架自动分配 ID, 默认是 DefaultIdentifierGenerator 雪花算法
     * ASSIGN_UUID 自动分配 UUID
     *
     * 自定义 Id 生成, 类是IdentifierGenerator，- 适用于不依赖数据库，用户自定义的主键生成场景
     *              另一类是IKeyGenerator - 依赖数据库，通过执行sql语句生成主键的场景
     */
    @Schema(description = "primary key")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    // @NotBlank(message = "id cannot be blank when update", groups = GroupValidation.Update.class)
    private ID id;
}
