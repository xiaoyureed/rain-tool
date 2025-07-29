package org.eu.rainx0.raintool.core.common.model;

import lombok.AllArgsConstructor;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 10:40
 */
@AllArgsConstructor
public class PageInfo implements IPageInfo{
    private Integer pageNo;
    private Integer pageSize;
    private String pageOrder;

    @Override
    public Integer pageNo() {
        return this.pageNo;
    }

    @Override
    public Integer pageSize() {
        return this.pageSize;
    }

    @Override
    public String pageOrder() {
        return this.pageOrder;
    }
}
