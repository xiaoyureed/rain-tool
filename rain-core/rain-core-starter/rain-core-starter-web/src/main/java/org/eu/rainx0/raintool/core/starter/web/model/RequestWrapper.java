package org.eu.rainx0.raintool.core.starter.web.model;

import java.io.Serializable;
import java.util.LinkedHashMap;


import org.eu.rainx0.raintool.core.starter.util.BeanTools;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 19:12
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RequestWrapper extends LinkedHashMap<String, Object> implements Serializable {
    public <T> T to(Class<T> clazz) {
        return BeanTools.toBean(this, clazz);
    }
}
