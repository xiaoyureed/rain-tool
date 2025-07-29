package org.eu.rainx0.raintool.core.starter.data.mybatis.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 21:12
 */
@Getter
@Setter
public abstract class AbstractAuditEntity<ID> extends AbstractIdEntity<ID> {
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "create time")
    private LocalDateTime createdAt;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "update time")
    private LocalDateTime updatedAt;
    /**
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    @Schema(description = "create user id")
    private String createdBy;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    @Schema(description = "update user id")
    private String updatedBy;
    /**
     * 是否删除
     */
    @TableLogic(delval = "1", value = "0")
    @Schema(description = "logic delete flag")
    private Integer deleted;
}