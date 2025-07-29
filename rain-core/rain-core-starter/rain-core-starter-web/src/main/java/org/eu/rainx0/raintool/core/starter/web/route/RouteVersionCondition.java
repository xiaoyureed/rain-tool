package org.eu.rainx0.raintool.core.starter.web.route;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eu.rainx0.raintool.core.common.Consts;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 17:01
 */
@AllArgsConstructor
public class RouteVersionCondition implements RequestCondition<RouteVersionCondition> {

    private final static Pattern VERSION_PATTERN = Pattern.compile("v(\\d+)"); // 版本号正则表达式

    private String version;

    /**
     * 处理合并的情况 (如方法, 类上都标注了注解)
     */
    @Override
    public RouteVersionCondition combine(RouteVersionCondition other) {
        // 方法上的注解优先 (Spring 会先读取 Controller 类上的条件（@RouteVersion("v1")），然后再读取方法上的（@RouteVersion("v2")）)
        // return new RouteVersionCondition(other.version);

        // 合并策略是: 采用最新版的 version
        String ver = maxVersion(this.version, other.version);
        return new RouteVersionCondition(ver);
    }

    private String maxVersion(String v1, String v2) {
        Integer version1 = Integer.valueOf(v1.substring(1));
        Integer version2 = Integer.valueOf(v2.substring(1));
        return version1 >= version2 ? v1 : v2;
    }

    /**
     * 判断当前请求是否满足这个条件
     * 如果满足，就返回这个条件（用于后续匹配）。
     * 如果不满足，返回 null，说明该 handler 不匹配该请求
     */
    @Override
    public RouteVersionCondition getMatchingCondition(HttpServletRequest request) {
        Matcher matcher = VERSION_PATTERN.matcher(request.getRequestURI());
        String version;
        if (matcher.find()) {
            version = matcher.group(1);
        } else {
            version = request.getHeader(Consts.Web.Headers.API_VERSION);
        }

        if (StringUtils.hasText(version) && version.equals(this.version)) {
            return this;
        }

        return null;
    }

    @Override
    public int compareTo(RouteVersionCondition other, HttpServletRequest request) {
        return 0; // 表示无需排序
    }
}
