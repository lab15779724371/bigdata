package com.tfit.BdBiProcSrvShEduOmc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：
 * @author: tianfang_infotech
 * @date: 2019/1/3 17:38
 */
public class IPUtil {

    private static Logger logger = LoggerFactory.getLogger(IPUtil.class);
    private static final String SPLIT_SYMBOL = ",";
    private static final long MULTIPLE_IP_LENGTH = 15;

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件：不能通过 request.getRemoteAddr() 获取IP地址
     * 如果使用多级反向代理：X-Forwarded-For值不止一个，第一个非unknown有效IP字符串，为真实IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.error("IPUtil ERROR ", e);
        }

        //使用代理，则获取第一个IP地址
        if (StringUtils.isEmpty(ip) && ip.length() > MULTIPLE_IP_LENGTH) {
            if (ip.indexOf(SPLIT_SYMBOL) > 0) {
                ip = ip.substring(0, ip.indexOf(SPLIT_SYMBOL));
            }
        }

        return ip;
    }
}