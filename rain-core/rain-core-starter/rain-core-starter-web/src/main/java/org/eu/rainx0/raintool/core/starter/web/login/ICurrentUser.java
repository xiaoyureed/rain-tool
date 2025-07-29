package org.eu.rainx0.raintool.core.starter.web.login;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 22:08
 */
public interface ICurrentUser<T> {
    T user();

    String identifier();
}
