package com.tfit.BdBiProcSrvShEduOmc.config;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;

/**
 * @Descritpion：
 * @author: tianfang_infotech
 * @date: 2019/1/21 13:19
 */
public class UserAuthConfig {

    /**
     * 请求头 token 信息
     */
    @Value("${auth.user.header-token}")
    private String headerToken;

    public String getToken(HttpServletRequest request) {
        return request.getHeader(this.getHeaderToken());
    }

    public String getHeaderToken() {
        return headerToken;
    }

    public void setHeaderToken(String headerToken) {
        this.headerToken = headerToken;
    }
}
