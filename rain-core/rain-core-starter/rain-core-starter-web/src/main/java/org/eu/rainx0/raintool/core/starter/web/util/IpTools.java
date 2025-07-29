package org.eu.rainx0.raintool.core.starter.web.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 10:33
 */
public class IpTools {
    /**
     * X-Forwarded-For是用于记录代理信息的,每经过一级代理，该字段就会记录来源地址,经过多级代理，服务端就会记录每级代理的X-Forwarded-For信息，IP之间以“，”分隔开。
     * 所以，我们只要获取X-Forwarded-For中的第一个IP，就是用户的真实IP。
     */
    public static String getIpFromRequest(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            return null;
        }

        int index = ip.indexOf(',');

        if (index == -1) {
            return ip;
        }

        //只获取第一个值
        return ip.substring(0, index);
    }

    public static String getSelfIp() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            //todo
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                // 忽略 loopback 和虚拟网卡
                if (iface.isLoopback() || iface.isVirtual() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("获取本机IP失败", e);
        }
        return getSelfIp();
    }
}
