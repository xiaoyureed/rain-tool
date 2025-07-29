package org.eu.raintool.example.hello.serviceapi;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/2 13:44
 */
@Data
@Schema(description = "hello 响应参数")
public class HelloR {
    private String id;
    private LocalDateTime time;
}
