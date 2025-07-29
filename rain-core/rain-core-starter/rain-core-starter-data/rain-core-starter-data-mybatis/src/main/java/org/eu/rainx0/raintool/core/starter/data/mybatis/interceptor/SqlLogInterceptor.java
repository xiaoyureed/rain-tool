package org.eu.rainx0.raintool.core.starter.data.mybatis.interceptor;

import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.eu.rainx0.raintool.core.starter.data.mybatis.model.MybatisProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * xiaoyureed@gmail.com
 * usage: https://www.cnblogs.com/Courage129/p/14121453.html
 * https://www.cnblogs.com/liangyun/p/17078401.html
 * <p>
 * 有四个方法调用可供被拦截:
 * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
 * ParameterHandler (getParameterObject, setParameters)
 * ResultSetHandler (handleResultSets, handleOutputParameters)
 * StatementHandler (prepare, parameterize, batch, update, query)
 */
@Slf4j
// @Component
@Intercepts({
    @Signature(
        // 希望拦截的类
        type = StatementHandler.class,
        // 希望拦截的方法名
        method = "query",
        // 方法参数
        args = {Statement.class, ResultHandler.class}
    ),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlLogInterceptor implements Interceptor {

    private static final Set<String> NEED_BRACKETS =
        Set.of("String", "Date", "Time", "LocalDate", "LocalTime", "LocalDateTime", "BigDecimal", "Timestamp");

    private Configuration configuration = null;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            // 如需打印结果, 返回值即结果集
            return invocation.proceed();
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            String sql = this.getSql(invocation.getTarget());
            log.debug(";;sql took {}ms: {}", cost, sql);
        }
    }


    /**
     * 获取sql
     */
    private String getSql(Object target) {
        try {
            StatementHandler statementHandler = (StatementHandler) target;
            BoundSql boundSql = statementHandler.getBoundSql();
            if (configuration == null) {
                final ParameterHandler parameterHandler = statementHandler.getParameterHandler();
                this.configuration = (Configuration) FieldUtils.readField(parameterHandler, "configuration", true);
            }
            // 替换参数格式化Sql语句，去除换行符
            return formatSql(boundSql, configuration);
        } catch (Exception e) {
            log.warn("!!! get sql error {}", target, e);
            return "failed to parse sql";
        }
    }

    /**
     * 获取完整的sql实体的信息
     */
    private String formatSql(BoundSql boundSql, Configuration configuration) {
        // 已经可以拿到 sql 了, 但是有换行, 不够美观
        String sql = boundSql.getSql();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        // 输入sql字符串空判断
        if (StringUtils.isEmpty(sql) || Objects.isNull(configuration)) {
            return "";
        }

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        // 替换空格容易造成本身存在空格的查询条件被替换
        sql = sql.replaceAll("[\n\r ]+", " ");

        if (parameterMappings == null) {
            return sql;
        }

        parameterMappings = parameterMappings.stream().filter(it -> it.getMode() != ParameterMode.OUT).collect(Collectors.toList());

        final StringBuilder result = new StringBuilder(sql);

        // 解析问号并填充
        for (int i = result.length(); i > 0; i--) {
            if (result.charAt(i - 1) != '?') {
                continue;
            }
            ParameterMapping parameterMapping = parameterMappings.get(parameterMappings.size() - 1);
            Object value;
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (parameterObject == null) {
                value = null;
            } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            if (value != null) {
                String type = value.getClass().getSimpleName();
                if (NEED_BRACKETS.contains(type)) {
                    result.replace(i - 1, i, "'" + value + "'");
                } else {
                    result.replace(i - 1, i, value.toString());
                }
            } else {
                result.replace(i - 1, i, "null");
            }
            parameterMappings.remove(parameterMappings.size() - 1);
        }
        return result.toString();
    }
}