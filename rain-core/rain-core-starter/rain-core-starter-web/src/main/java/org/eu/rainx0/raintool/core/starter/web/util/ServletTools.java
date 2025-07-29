package org.eu.rainx0.raintool.core.starter.web.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eu.rainx0.raintool.core.common.Consts;
import org.eu.rainx0.raintool.core.common.model.IPageInfo;
import org.eu.rainx0.raintool.core.common.model.PageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 18:48
 */
@Slf4j
public class ServletTools {

    public static String getPostBody() throws IOException {
        String ret = getRequest().getReader()
            .lines()
            .collect(Collectors.joining(System.lineSeparator()));
            // .collect(Collectors.joining());
        return ret;
    }

    /**
     * 获取用户请求的语言，在http请求中，会在Header中的Accept-Language指定语言
     * 如果不指定，则返货服务器的机器语言
     */
    public static Locale getRequestLanguage() {
        Locale language = Locale.getDefault(); // 默认获取机器语言
        try {
            String acceptLanguage = getRequestHeader("Accept-Language");
            if (acceptLanguage != null) {
                language = Locale.forLanguageTag(acceptLanguage);
            }
        } catch (Exception e) {
            log.warn(";;Take the default language in use");
        }
        return language;
    }

    public static Optional<String> getAuthenticationToken() {
        return getAuthenticationToken(Consts.Web.Headers.AUTH, true);
    }

    public static Optional<String> getAuthenticationToken(String key, boolean bearerPrefixed) {
        String token = getRequestHeader(key);

        if (!StringUtils.hasText(token)) {
            token = getRequestParam(key);
        }

        if (!StringUtils.hasText(token)) {
            log.error(";;Token missing");
            return Optional.empty();
        }

        final String bearer = "Bearer ";
        if (bearerPrefixed && !token.startsWith(bearer)) {
            log.error(";;token format is wrong: {}", token);
            return Optional.empty();
        }

        if (token.startsWith(bearer)) {
            token = token.substring(bearer.length());
        }

        return Optional.of(token);
    }


    public static Map<String, String> getRequestHeaders() {
        HashMap<String, String> result = new LinkedHashMap<>();
        HttpServletRequest request = getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            result.put(headerName, headerValue);
        }

        return result;
    }

    public static String getRequestHeader(String header) {
        return getRequest().getHeader(header);
    }

    public static String getRequestParam(String paramName) {
        return getRequest().getParameter(paramName);
    }


    public static String getRequestInfo() {
        HttpServletRequest request = getRequest();
        String urlParams = request.getQueryString();

        String ret = request.getMethod() + ":" + request.getRequestURI();

        if (StringUtils.hasText(urlParams) && !urlParams.equals("null")) {
            ret = ret + "?" + urlParams;
        }

        return ret;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(requestAttributes).getRequest();
    }


    public static void redirect(HttpServletResponse resp, String path) {
        try {
            resp.sendRedirect(path);
        } catch (IOException e) {
            log.error(";;send redirect error. path: {}. err: {}", path, e.toString());
        }
    }

    public static IPageInfo getPageInfo() {
        HttpServletRequest request = getRequest();

        String pageSizeRaw = request.getHeader(Consts.Web.Headers.PAGE_SIZE);
        String pageNoRaw = request.getHeader(Consts.Web.Headers.PAGE_NO);
        String pageOrder = request.getHeader(Consts.Web.Headers.PAGE_ORDER);

        if (!StringUtils.hasText(pageSizeRaw)) {
            pageSizeRaw = request.getParameter(Consts.Web.RequestParams.PAGE_SIZE);
        }
        if (!StringUtils.hasText(pageNoRaw)) {
            pageNoRaw = request.getParameter(Consts.Web.RequestParams.PAGE_NO);
        }
        if (!StringUtils.hasText(pageOrder)) {
            pageOrder = request.getParameter(Consts.Web.RequestParams.PAGE_ORDER);
        }

        Integer pageSize;
        Integer pageNo;
        try {
            pageSize = Integer.valueOf(pageSizeRaw);
            pageNo = Integer.valueOf(pageNoRaw);
        } catch (Exception e) {
            pageSize = Consts.Web.PageDefault.PAGE_SIZE;
            pageNo = Consts.Web.PageDefault.PAGE_NO;
        }

        return new PageInfo(pageNo, pageSize, pageOrder);
    }

}
