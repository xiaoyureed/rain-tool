package org.eu.rainx0.raintool.core.starter.data.jpa.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 21:00
 */

@Service
public class NativeSqlRepository {

    /**
     * 用于手动创建 entity Manager, 实现细致的控制
     */
    // @PersistenceUnit
    // private EntityManagerFactory emf;

    @PersistenceContext
    private EntityManager em;

    /**
     * Execute update sql
     */
    public Integer update(String sql, Map<String, Object> params) {
        Query query = em.createNativeQuery(sql);
        setParams(query, params);
        int rows = query.executeUpdate();

        // 不要手动关闭, 交给 spring 处理
        // em.close();

        return rows;
    }

    public <T> List<T> queryList(String sql, Map<String, Object> params, Class<T> clazz) {
        Query nativeQuery = em.createNativeQuery(sql, clazz);
        setParams(nativeQuery,  params);
        List resultList = nativeQuery.getResultList();
        return resultList;
    }

    /**
     * SELECT id, username name, enabled FROM account WHERE enabled = :enabled
     */
    public List<Map<String, Object>> queryList(String sql, Map<String, Object> params) {
        Query nativeQuery = em.createNativeQuery(sql);
        setParams(nativeQuery, params);

        // each element represents a row, each row is an Object[] or object (single col)
        List resultList = nativeQuery.getResultList();
        if (CollectionUtils.isEmpty(resultList)) {
            return List.of();
        }

        List<String> columnNames = getColumnNames(sql);

        List<Map<String, Object>> ret = new ArrayList<>(resultList.size());

        for (Object rowObj : resultList) {
            Object[] row;
            if (rowObj instanceof Object[]) {
                row = (Object[]) rowObj;
            } else {
                row = new Object[]{rowObj};
            }
            Map<String, Object> r = new LinkedHashMap<>(row.length);
            for (int i = 0; i < row.length; i++) {
                Object colValue = row[i];
                // Ensure no out of index error
                String colName = columnNames.size() > i ? columnNames.get(i) : "col" + i;
                r.put(colName, colValue);
            }

            ret.add(r);

        }
        return ret;

    }

    public Map<String, Object> queryOne(String sql, Map<String, Object> params) {
        List<Map<String, Object>> rows = this.queryList(sql, params);
        if (CollectionUtils.isEmpty(rows)) {
            return Collections.emptyMap();
        }
        return rows.get(0);
    }

    // 可选：简单提取 select 字段名（推荐用别名），也可以忽略
    private List<String> getColumnNames(String sql) {
        // 简单提取 SELECT 字段列表（示例用途，不适用于复杂 SQL）
        int fromIndex = sql.toLowerCase().indexOf("from");
        if (fromIndex == -1) return List.of();

        String columnPart = sql.substring(6, fromIndex).trim();
        return Arrays.stream(columnPart.split(","))
            .map(String::trim)
            .map(col -> {
                String[] parts = col.split(" ");
                return parts.length > 1 ? parts[parts.length - 1] : parts[0];
            })
            .toList();
    }

    private void setParams(Query query, Map<String, Object> params) {
        if (params == null || CollectionUtils.isEmpty(params)) {
            return;
        }
        params.forEach(query::setParameter);
    }
}