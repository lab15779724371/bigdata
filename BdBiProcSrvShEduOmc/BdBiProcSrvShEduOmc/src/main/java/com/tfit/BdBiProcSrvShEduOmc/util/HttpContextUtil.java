package com.tfit.BdBiProcSrvShEduOmc.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：
 * @author: tianfang_infotech
 * @date: 2019/1/3 17:36
 */
public class HttpContextUtil {

    /**
     * 获取请求上下文
     * @return
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
