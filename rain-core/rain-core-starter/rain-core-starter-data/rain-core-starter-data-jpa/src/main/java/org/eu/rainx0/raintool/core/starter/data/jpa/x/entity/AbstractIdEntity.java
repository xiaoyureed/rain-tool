package org.eu.rainx0.raintool.core.starter.data.jpa.x.entity;

import java.io.Serializable;

import org.eu.rainx0.raintool.core.common.util.RandomTools;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 20:53
 */
@MappedSuperclass // Flag as an abstract entity
@Data
public abstract class AbstractIdEntity implements Serializable {

    @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    //
    // 自定义 uuid 生成
    @GeneratedValue(generator = "PK_Generator")
    @GenericGenerator(name = "PK_Generator", type = PrimaryKeyGenerator.class)
    protected String id;

    public static class PrimaryKeyGenerator implements IdentifierGenerator {
        @Override
        public Object generate(
            /*db connection*/SharedSessionContractImplementor sharedSessionContractImplementor,
            /*entity to be saved*/Object o) {
            return RandomTools.uuid32();
        }

    }

    public static class SequentialUuidHexGenerator {

        private static final String SEP = "-";

        private static short counter = 0;

        /**
         *
         */
        public static String format(int intValue) {
            // 16 进制
            String formatted = Integer.toHexString(intValue);

            StringBuffer buf = new StringBuffer("00000000");// int 长度 8bit
            buf.replace(8 - formatted.length(), 8, formatted);

            return buf.toString();
        }

        public static String format(short shortValue) {
            String formatted = Integer.toHexString(shortValue);
            StringBuffer buf = new StringBuffer("0000"); // short 长度 4bit
            buf.replace(4 - formatted.length(), 4, formatted);
            return buf.toString();
        }

        public static String generate() {
            String uuid = format(jvm()) + SEP
                + format(getHiTime()) + SEP
                + format(getLoTime()) + SEP
                + format(getCount());
            return uuid;
        }

        public static short getCount() {
            synchronized (SequentialUuidHexGenerator.class) {
                short current = counter;

                counter = (short) (current + 1);

                return current;
            }
        }

        public static int jvm() {
            return (int) (System.currentTimeMillis() >>> 8);
        }

        public static short getHiTime() {
            return (short) ((int) (System.currentTimeMillis() >>> 32));
        }

        public static int getLoTime() {
            return (int) System.currentTimeMillis();
        }
    }
}



