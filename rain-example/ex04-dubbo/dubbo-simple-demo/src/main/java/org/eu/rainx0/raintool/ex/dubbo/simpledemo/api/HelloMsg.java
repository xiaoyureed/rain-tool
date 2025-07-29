package org.eu.rainx0.raintool.ex.dubbo.simpledemo.api;

import java.io.Serializable;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/7/25 09:26
 */
@Data
public class HelloMsg implements Serializable {
    private String id;
    private String msg;
}
