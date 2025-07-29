package org.eu.rainx0.raintool.core.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 10:39
 */
public class ListTools {
    /**
     * 匹配两个list集合相同元素，并执行匹配后的动作
     * 适用于两个集合泛型不一致
     *
     * @param list1    第一个集合
     * @param list2    第二个集合
     * @param field1   第一个集合中判定元素相同的属性，如：SomeBean::getId
     * @param field2   第二个集合中判定元素相同的属性，如：OtherBean::getId
     * @param operator 匹配后执行的动作，入参为A、B集合中的相同元素，如：(a, b)->a.setXx(b.getXx())
     * @param <T>      第一个集合的泛型
     * @param <K>      第二个集合的泛型
     */
    public static <T, K> void match(Collection<T> list1, Collection<K> list2, Function<T, ?> field1, Function<K, ?> field2, BiConsumer<T, K> operator) {
        Map<?, K> map = list2.stream().collect(Collectors.toMap(field2, Function.identity()));
        list1.forEach(item1 -> Optional.ofNullable(map.get(field1.apply(item1))).ifPresent(item2 -> operator.accept(item1, item2)));
    }

    /**
     * 匹配两个list集合中的相同元素，并执行匹配后的动作
     * 适用于两个集合的泛型一致
     *
     * @param list1      第一个集合
     * @param list2      第二个集合
     * @param equalField 判定元素相同的属性，如Student::getId
     * @param operator   匹配后执行的动作，入参为两个集合中的相同元素，如：(a, b)->a.setXx(b.getXx())
     * @param <T>        集合泛型
     */
    public static <T> void match(Collection<T> list1, Collection<T> list2, Function<T, ?> equalField, BiConsumer<T, T> operator) {
        match(list1, list2, equalField, equalField, operator);
    }

    /**
     * 转换List
     *
     * @param list      原集合
     * @param converter 转换函数
     * @return 转换后的函数
     */
    public static <T, R> List<R> convert(List<T> list, Function<T, R> converter) {
        return list.stream().map(converter).collect(Collectors.toList());
    }

    /**
     * 转换List，转换之后去重
     *
     * @param list      原集合
     * @param converter 转换函数
     * @return 转换后的函数
     */
    public static <T, R> List<R> convertDistinct(List<T> list, Function<T, R> converter) {
        return list.stream().map(converter).distinct().collect(Collectors.toList());
    }

    public interface TreeNode<T extends TreeNode<T>> {

        /**
         * 获取id
         *
         * @return long
         */
        Long getId();

        /**
         * 设置节点id
         *
         * @param id id
         */
        void setId(Long id);

        /**
         * 获取父节点id
         *
         * @return 父节点id
         */
        Long getParentId();

        /**
         * 设置父节点id
         *
         * @param parentId 父节点id
         */
        void setParentId(Long parentId);

        /**
         * 获取子节点列表
         *
         * @return list
         */
        List<T> getChildList();

        /**
         * 设置子节点列表
         *
         * @param childList 子节点列表
         */
        void setChildList(List<T> childList);
    }

    /**
     * List转为树
     *
     * @param source list
     * @param rootId 指定树的根节点id，一般为0
     * @return 树化后的List
     */
    public static <T extends TreeNode<T>> List<T> toTree(List<T> source, Long rootId) {

        if (source == null || source.size() == 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        Map<Object, T> map = new HashMap<>(source.size());
        source.forEach(node -> {
            if (Objects.equals(rootId, node.getParentId())) {
                result.add(node);
            }
            map.put(node.getId(), node);
        });
        source.forEach(node -> map.computeIfPresent(node.getParentId(), (parentId, parentNode) -> {
            Optional.ofNullable(parentNode.getChildList()).orElseGet(() -> {
                List<T> list = new ArrayList<>();
                parentNode.setChildList(list);
                return list;
            }).add(node);
            return parentNode;
        }));
        return result;
    }

    /**
     * List转为Map（可指定map key的生成方式，value为元素本身）
     *
     * <p>- map的key通过指定函数生成，value为list中的元素
     * <p>- 如果List中含有相同元素，转成map后会只留一个
     * <p>- 注意，list中的元素不能为null，否则报NPE
     *
     * @param source       list
     * @param keyGenerator map中key的生成函数
     * @return map
     */
    public static <K, V> Map<K, V> toMap(List<V> source, Function<V, K> keyGenerator) {
        return source.stream().collect(Collectors.toMap(keyGenerator, v -> v, (v1, v2) -> v2, () -> new HashMap<>((int) (source.size() / 0.75))));
    }

    /**
     * List转为Map（可指定map key、value的生成方式）
     *
     * @param source         list
     * @param keyGenerator   map中key的生成函数
     * @param valueGenerator map中value的生成函数
     * @return map
     */
    public static <R, K, V> Map<K, V> toMap(List<R> source, Function<R, K> keyGenerator, Function<R, V> valueGenerator) {
        return source.stream().collect(Collectors.toMap(keyGenerator, valueGenerator, (v1, v2) -> v2, () -> new HashMap<>((int) (source.size() / 0.75))));
    }

    /**
     * 按指定字段正向排序
     * <p>注：值为null的排在最后
     *
     * @param source       原集合
     * @param keyExtractor 排序的字段
     */
    public static <T, U extends Comparable<U>> void sort(List<T> source, Function<? super T, ? extends U> keyExtractor) {
        Comparator<T> comparing = Comparator.nullsLast(Comparator.comparing(keyExtractor));
        source.sort(comparing);
    }

    /**
     * 按指定字段反向排序
     * <p>注：值为空的排在最后
     *
     * @param source       原集合
     * @param keyExtractor 排序的字段
     */
    public static <T, U extends Comparable<U>> void sortReverse(List<T> source, Function<? super T, ? extends U> keyExtractor) {
        Comparator<T> comparing = Comparator.nullsLast(Comparator.comparing(keyExtractor).reversed());
        source.sort(comparing);
    }
}
