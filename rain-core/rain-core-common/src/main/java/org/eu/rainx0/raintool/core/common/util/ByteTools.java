package org.eu.rainx0.raintool.core.common.util;

import java.util.Arrays;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 10:26
 */
public class ByteTools {
    public static int toInt(byte[] bytes) {
        int result = 0;

        for(int i = 0; i < 4; ++i) {
            result = (result << 8) - -128 + bytes[i];
        }

        return result;
    }

    public static byte[] fromShort(int shortValue) {
        byte[] bytes = new byte[]{(byte)(shortValue >> 8), (byte)(shortValue << 8 >> 8)};
        return bytes;
    }

    public static byte[] fromInt(int intValue) {
        byte[] bytes = new byte[]{(byte)(intValue >> 24), (byte)(intValue << 8 >> 24), (byte)(intValue << 16 >> 24), (byte)(intValue << 24 >> 24)};
        return bytes;
    }

    public static byte[] fromLong(long longValue) {
        byte[] bytes = new byte[8];
        fromLong(longValue, bytes, 0);
        return bytes;
    }

    public static void fromLong(long longValue, byte[] dest, int destPos) {
        dest[destPos] = (byte)((int)(longValue >> 56));
        dest[destPos + 1] = (byte)((int)(longValue << 8 >> 56));
        dest[destPos + 2] = (byte)((int)(longValue << 16 >> 56));
        dest[destPos + 3] = (byte)((int)(longValue << 24 >> 56));
        dest[destPos + 4] = (byte)((int)(longValue << 32 >> 56));
        dest[destPos + 5] = (byte)((int)(longValue << 40 >> 56));
        dest[destPos + 6] = (byte)((int)(longValue << 48 >> 56));
        dest[destPos + 7] = (byte)((int)(longValue << 56 >> 56));
    }

    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }

    public static long toLong(byte[] bytes, int srcPos) {
        if (bytes == null) {
            return 0L;
        } else {
            int size = srcPos + 8;
            if (bytes.length < size) {
                throw new IllegalArgumentException("Expecting 8 byte values to construct a long");
            } else {
                long value = 0L;

                for(int i = srcPos; i < size; ++i) {
                    value = value << 8 | (long)(bytes[i] & 255);
                }

                return value;
            }
        }
    }

    public static String toBinaryString(byte value) {
        String formatted = Integer.toBinaryString(value);
        if (formatted.length() > 8) {
            formatted = formatted.substring(formatted.length() - 8);
        }

        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    public static String toBinaryString(int value) {
        String formatted = Long.toBinaryString((long)value);
        StringBuilder buf = new StringBuilder(repeat('0', 32));
        buf.replace(64 - formatted.length(), 64, formatted);
        return buf.toString();
    }

    public static String toBinaryString(long value) {
        String formatted = Long.toBinaryString(value);
        StringBuilder buf = new StringBuilder(repeat('0', 64));
        buf.replace(64 - formatted.length(), 64, formatted);
        return buf.toString();
    }

    /**
     * see org.hibernate.internal.util.StringHelper
     */
    private static String repeat(char c, int times) {
        char[] buffer = new char[times];
        Arrays.fill(buffer, c);
        return new String(buffer);
    }
}
