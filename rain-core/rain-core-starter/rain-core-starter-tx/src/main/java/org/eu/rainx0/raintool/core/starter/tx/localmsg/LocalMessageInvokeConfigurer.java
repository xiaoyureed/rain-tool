package org.eu.rainx0.raintool.core.starter.tx.localmsg;

import java.util.concurrent.Executor;

/**
 * @author xiaoyu
 * @time 2025/7/31 11:27
 */
public interface LocalMessageInvokeConfigurer {
    default Executor getAsyncExecutor() {
        return null;
    }
}
