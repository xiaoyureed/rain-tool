package org.eu.rainx0.raintool.core.common;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 08:41
 */
public interface Consts {
    interface Web {
        interface Headers {
            // 若是通过 feign 发送的内部请求, 则添加此 header 标识, 值为 "true"
            String FEIGN_FLAG = "x-feign";
            String REQUEST_ID = "x-request-id";
            String AUTH = "Authorization";
            String USER_ID = "x-user-id";
            String API_VERSION = "x-api-version";
            String IP = "x-ip";
            String PAGE_SIZE = "x-page-size";
            String PAGE_NO = "x-page-no";
            String PAGE_ORDER = "x-page-order";
        }

        interface RequestParams {
            String PAGE_SIZE = "pageSize";
            String PAGE_NO = "pageNo";
            String PAGE_ORDER = "pageOrder";
        }

        interface PageDefault {
            Integer PAGE_SIZE = 20;
            Integer PAGE_NO = 1;
            String PAGE_ORDER = "desc";
        }

    }


}
