package com.icuxika.framework.config.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {

    /**
     * 获取IP地址
     * 当 Nginx 运行在 Docker 中时，如果 Docker 网络不是 host 模式，那么 x-forwarded-for 存储的将是容器内网关地址
     *
     * @param request HttpServletRequest
     * @return ip
     */
    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0];
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
