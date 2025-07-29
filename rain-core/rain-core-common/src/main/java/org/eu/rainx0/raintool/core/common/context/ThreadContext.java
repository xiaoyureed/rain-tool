package org.eu.rainx0.raintool.core.common.context;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Note: To make this context work, we need to use TtlExecutors as the async executor
 * @author: xiaoyu
 * @time: 2025/6/30 09:38
 */
public class ThreadContext {
    // transmittable-thread-local is used to support passing vars in multiple threads environment
    private static final TransmittableThreadLocal<Map<String, Object>> CONTEXT_HOLDER =
        new TransmittableThreadLocal<>() {
            @Override
            protected Map<String, Object> initialValue() {
                return new HashMap<>();
            }
        };

    public static void set(String key, Object value) {
        CONTEXT_HOLDER.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT_HOLDER.get().get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> type) {
        return (T) CONTEXT_HOLDER.get().get(key);
    }

    public static void remove(String key) {
        CONTEXT_HOLDER.get().remove(key);
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public static boolean contains(String key) {
        return CONTEXT_HOLDER.get().containsKey(key);
    }

    public static Map<String, Object> getAll() {
        return new HashMap<>(CONTEXT_HOLDER.get());
    }
}
