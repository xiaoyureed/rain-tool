package org.eu.rainx0.raintool.core.common.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type 子类
 *  Class<T>
 *  GenericArrayType 泛型数组, 如 List<String>[], T[]...
 *  WildCardType    泛型表达式, 如 List<? extends String> 中的 ? extends String  , 不是一种 Java 类型
 *  ParameterizedType 参数化类型, List<String>, Map<K,V>
 *  TypeVariable 泛型变量, 如 List<T> 中的 T
 *
 *
 *
 * @author xiaoyu
 * @time 2025/7/19 17:51
 */
public abstract class BaseDao<T> {
    private Class<T> clazz; // 表示泛型参数的class 类型

    public BaseDao() {
        //泛型列表
        // Type[] genericInterfaces = this.getClass().getGenericInterfaces();

        // 获取当前类的父类的 Type类型, 若有泛型, 则是 ParameterizedType 类型
        Type parentType = this.getClass().getGenericSuperclass();
        if (parentType instanceof ParameterizedType) {
            ParameterizedType parentParameterizedType = (ParameterizedType) parentType;

            // 获取泛型参数
            Type[] actualTypeArguments = parentParameterizedType.getActualTypeArguments();

            clazz = (Class<T>) actualTypeArguments[0];
        } else {
            throw new RuntimeException("请使用泛型");
        }
    }
}
