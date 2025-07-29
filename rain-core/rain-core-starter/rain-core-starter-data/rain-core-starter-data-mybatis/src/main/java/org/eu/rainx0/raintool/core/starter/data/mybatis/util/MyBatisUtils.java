package org.eu.rainx0.raintool.core.starter.data.mybatis.util;

import java.util.Collection;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.core.common.model.IPageInfo;
import org.eu.rainx0.raintool.core.common.model.SortItem;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 09:21
 */
public class MyBatisUtils {
    private static final String MYSQL_ESCAPE_CHARACTER = "`";

    public static <T> Page<T> buildPage(IPageInfo pageInfo) {
        return buildPage(pageInfo, null);
    }

    public static <T> Page<T> buildPage(IPageInfo pageInfo, Collection<SortItem> sortingFields) {
        // 页码 + 数量
        Page<T> page = new Page<>(pageInfo.pageNo(), pageInfo.pageSize());

        // 排序字段
        if (!CollectionUtils.isEmpty(sortingFields)) {
            page.addOrder(
                sortingFields.stream()
                    .map(item ->
                        item.isAsc() ? OrderItem.asc(item.getColumn()) : OrderItem.desc(item.getColumn())
                    )
                    .collect(Collectors.toList())
            );
        }

        return page;
    }

    /**
     * 将拦截器添加到链中
     * 由于 MybatisPlusInterceptor 不支持添加拦截器，所以只能全量设置      ?
     *
     * @param interceptor 链
     * @param inner 拦截器
     * @param index 位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
//        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
//        inners.add(index, inner);
//        interceptor.setInterceptors(inners);
        interceptor.addInnerInterceptor(inner);
    }

    /**
     * 获得 Table 对应的表名
     *
     * @param table 表
     * @return 去除转移字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();

        //     * 兼容 MySQL 转义表名 `t_xxx`
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }

        return tableName;
    }

    /**
     * 构建 Column 对象
     *
     * @param tableName 表名
     * @param tableAlias 别名
     * @param column 字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }
        return new Column(tableName + StringPool.DOT + column);
    }
}
