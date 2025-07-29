package org.eu.rainx0.raintool.core.starter.data.mybatis.x;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.apache.ibatis.reflection.MetaObject;
import org.eu.rainx0.raintool.core.common.context.LoginContext;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.AbstractAuditEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 21:20
 */
@Component
@Slf4j
public class AutoFillHandler implements MetaObjectHandler {

    /**
     * entity 填充属性名
     */
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_CREATED_BY = "createdBy";
    private static final String FIELD_UPDATED_AT = "updatedAt";
    private static final String FIELD_UPDATED_BY = "updatedBy";
    private static final String FIELD_DELETED = "deleted";

    @Override
    public void insertFill(MetaObject metaObject) {

        // Object originalObject = metaObject.getOriginalObject();
        // if (!(originalObject instanceof AbstractAuditEntity<?>)) {
        //     return;
        // }
        // AbstractAuditEntity<?> entity = (AbstractAuditEntity<?>) originalObject;

        // 采用另一种方式填充字段
        // Object updateTime = getFieldValByName("updateTime", metaObject);
        // if (updateTime == null) {
        //     setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        //     log.debug("!!! mybatis plus auto fill, updateTime ok");
        // }

        LocalDateTime now = LocalDateTime.now();
        String curUserId = curUserId();

        if (metaObject.hasSetter(FIELD_CREATED_AT)) { // 判断是否存在属性再执行填充, 可提高性能
            this.strictInsertFill(metaObject, FIELD_CREATED_AT, LocalDateTime.class, now);
        }
        if (metaObject.hasSetter(FIELD_UPDATED_AT)) {
            this.strictInsertFill(metaObject, FIELD_UPDATED_AT, LocalDateTime.class, now);
        }
        if (metaObject.hasSetter(FIELD_CREATED_BY)) {
            this.strictInsertFill(metaObject, FIELD_CREATED_BY, String.class, curUserId);
        }
        if (metaObject.hasSetter(FIELD_UPDATED_BY)) {
            this.strictInsertFill(metaObject, FIELD_UPDATED_BY, String.class, curUserId);
        }
        if (metaObject.hasSetter(FIELD_DELETED)) {
            this.strictInsertFill(metaObject, FIELD_DELETED, Integer.class, 0);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, FIELD_UPDATED_AT, LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, FIELD_UPDATED_BY, AutoFillHandler::curUserId, String.class);
    }

    private static String curUserId() {
        String currentUserId = LoginContext.get(String.class);

        if (StringUtils.hasText(currentUserId)) {
            return currentUserId;
        }

        log.error(";;No user info found in LoginContext");
        return "";

    }


}
