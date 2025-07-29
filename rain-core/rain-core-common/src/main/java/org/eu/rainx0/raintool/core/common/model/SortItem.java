package org.eu.rainx0.raintool.core.common.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 09:23
 */
@Data
public class SortItem implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 需要进行排序的字段
     */
    private String column;

    /**
     * 是否正序排列，默认 true
     */
    private boolean asc = true;

    public static SortItem asc(String column) {
        return build(column, true);
    }

    public static SortItem desc(String column) {
        return build(column, false);
    }

    public static List<SortItem> ascs(String... columns) {
        return Arrays.stream(columns).map(SortItem::asc).collect(Collectors.toList());
    }

    public static List<SortItem> descs(String... columns) {
        return Arrays.stream(columns).map(SortItem::desc).collect(Collectors.toList());
    }

    private static SortItem build(String column, boolean asc) {
        SortItem item = new SortItem();
        item.setColumn(column);
        item.setAsc(asc);

        return item;
    }
}
