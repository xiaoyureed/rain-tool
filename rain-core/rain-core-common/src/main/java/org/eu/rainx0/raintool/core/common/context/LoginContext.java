package org.eu.rainx0.raintool.core.common.context;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 09:42
 */
public class LoginContext {
    private static final String USER_KEY = "user";

    public static void set(Object user) {
        ThreadContext.set(USER_KEY, user);
    }

    public static <T> T get(Class<T> type) {
        return ThreadContext.get(USER_KEY, type);
    }

    public static void clear() {
        ThreadContext.remove(USER_KEY);
    }
}
