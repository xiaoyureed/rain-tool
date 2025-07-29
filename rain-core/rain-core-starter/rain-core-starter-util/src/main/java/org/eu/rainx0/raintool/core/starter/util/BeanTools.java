package org.eu.rainx0.raintool.core.starter.util;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/6/29 19:14
 */
@Slf4j
public class BeanTools {
    public static ObjectMapper objectMapper = new ObjectMapper();
    private static Map<String, BeanCopier> cache = new ConcurrentHashMap<>();

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_HOURS_PATTERN = "yyyy-MM-dd HH:mm";

    static {
        // 数字相关的类型，全部格式化成字符串
        // objectMapper.configure(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature(), true);

        // 反序列化, json string 有未知字段, 不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // todo 貌似没效果
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        // Include.Include.ALWAYS 默认 (总是序列化)
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
        // Include.NON_NULL 属性为NULL 不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 允许在 JSON 字符串中使用未加引号的控制字符（如换行符、制表符等）
        // objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        // 允许出现单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        // 兼容反斜杠，允许接受引号引起来的所有字符
        // objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

        objectMapper.registerModule(javaTimeModule());

        objectMapper.setVisibility(
            // 没有这个配置,如果一个类有 name、Name、NAME 三个属性,Jackson 默认只会处理 name,会忽略 Name 和 NAME。
            // 加上这个配置后,Jackson 会处理 name、Name 和 NAME 三个属性,无论它们的大小写。
            PropertyAccessor.ALL,
            JsonAutoDetect.Visibility.ANY // private & public 属性都会包含进来, 被序列化
        );

        // 不需要反序列化时 (如作为 rest api 返回值)：不设置activateDefaultTyping或者使用EXISTING_PROPERTY。
        // 当然, springmvc 使用的序列化是内置的, 不会用这里的
        // 需要反序列化时(如果作为缓存等)：一般使用 WRAPPER_ARRAY、WRAPPER_OBJECT、PROPERTY中的一种。如果不指定则默认使用的是WRAPPER_ARRAY。
        // objectMapper.activateDefaultTyping(
        //     objectMapper.getPolymorphicTypeValidator(),
        //     ObjectMapper.DefaultTyping.NON_FINAL, // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //     JsonTypeInfo.As.WRAPPER_ARRAY // 默认值, 可省略, 序列化后的 json 是一个数组, 两个元素, 第一个是全类名, 第二个是 json 对象
        // );

    }

    private static SimpleModule javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();

        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));

        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));


        return module;
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(";;Error of parsing ojb to string",  e);
            return null;
        }
    }

    /**
     * convert an object to a Bean with specified type
     */
    public static <T> T toBean(Object source, Class<T> target) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(source), target);
        } catch (JsonProcessingException e) {
            log.error(";;Error of convert source to [" + target.getName() + "]", e);
            return null;
        }
    }

    /**
     * Convert to a map
     */
    public static Map<String, Object> toMap(Object source)  {
        try {
            String input;

            if (source instanceof String) {
                input = (String) source;
            } else {
                input = objectMapper.writeValueAsString(source);
            }

            return objectMapper.readValue(input, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error(";;Error of convert bean [" + source.getClass().getName() + "] to map", e);
            return Collections.emptyMap();
        }
    }

    public static <S, T> List<T> copy(List<S> source, Class<T> target) {
        ArrayList<T> result = new ArrayList<>(source.size());

        for (S s : source) {
            T copy = copy(s, target, null);
            result.add(copy);
        }

        return result;
    }

    public static <S, T> List<T> copy(List<S> source, Class<T> target, Converter converter // from cglib core
    ) {
        ArrayList<T> result = new ArrayList<>(source.size());

        for (S s : source) {
            T copy = copy(s, target, converter);
            result.add(copy);
        }

        return result;
    }

    public static <S, T> T copy(S source, Class<T> target) {
        return copy(source, target, null);
    }

    /**
     * Note: this method is not compatible with lombok @Accessors(chain = true), https://github.com/cglib/cglib/issues/108
     */
    public static <S, T> T copy(S source, Class<T> target, Converter converter) {
        String key = generateKey(source, target);

        // double check to prevent the concurrent issue
        if (!cache.containsKey(key)) {
            synchronized (BeanTools.class) {
                if (!cache.containsKey(key)) {
                    try {
                        BeanCopier beanCopier = BeanCopier.create(source.getClass(), target, converter != null);
                        cache.put(key, beanCopier);
                    } catch (Exception e) {
                        log.warn("!!! bean copier creation error, fallback to use the copy util provided by spring");
                        T result = newInstance(target);
                        org.springframework.beans.BeanUtils.copyProperties(source, result);

                        return result;
                    }
                }
            }
        }


        T result = newInstance(target);
        cache.get(key).copy(source, result, converter);

        return result;
    }

    public static <T> T newInstance(Class<T> clazz) {
        Constructor<T> constructorNoArgs;

        try {
            constructorNoArgs = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            log.error(";;Class[{}] has no constructor with no args", clazz.getName(), e);
            throw new RuntimeException();
        }

        T result;

        try {
            result = constructorNoArgs.newInstance();
        } catch (Exception e) {
            log.error(";;Error of new instance from constructor [" + constructorNoArgs.getName() + "]", e);
            throw new RuntimeException();
        }

        return result;
    }

    private static String generateKey(Object source, Object target) {
        return source.getClass().getName() + target.getClass().getName();
    }
}
