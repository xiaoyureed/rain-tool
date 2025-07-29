package org.eu.rainx0.raintool.core.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiaoyu
 * @time 2025/7/11 18:37
 */
@Data
@Accessors(chain = true)
public class LoginInfo {
    private String userId;
    private String username;

    private String sessionId;
}
