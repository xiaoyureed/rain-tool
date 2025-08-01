package org.eu.rainx0.raintool.core.starter.tx.localmsg;

import org.eu.rainx0.raintool.core.common.context.ThreadContext;

/**
 * @author xiaoyu
 * @time 2025/7/31 12:12
 */
public class LocalMessageInvokeHolder {
    private static final String key = "local-msg-invoke";

    public static boolean isInvoking() {
        return ThreadContext.get(key, Boolean.class);
    }

    public static void setInvoking() {
        ThreadContext.set(key, true);
    }

    public static void setInvoked() {
        ThreadContext.remove(key);
    }
}
