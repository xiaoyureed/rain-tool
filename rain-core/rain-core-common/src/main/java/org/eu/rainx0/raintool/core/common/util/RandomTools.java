package org.eu.rainx0.raintool.core.common.util;

import java.util.Random;
import java.util.UUID;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 10:40
 */
public class RandomTools {
    private RandomTools() {}

    /**
     * 随机验证码
     */
    public static String numberString(int length) {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(10); // 小于 10 的整数
            code.append(num);
        }
        return code.toString();
    }

    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String uuidSorted32() {
        throw new RuntimeException("unsupported");
    }
}
