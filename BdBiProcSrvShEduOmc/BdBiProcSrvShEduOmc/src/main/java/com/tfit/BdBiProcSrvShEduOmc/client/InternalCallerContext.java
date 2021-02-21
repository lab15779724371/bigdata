package com.tfit.BdBiProcSrvShEduOmc.client;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.tfit.BdBiProcSrvShEduOmc.util.Base64Util;

//import com.ssic.cloud.exceptions.SSICRuntimeException;

public class InternalCallerContext {
    private String distributeThreadID;    // UUID
    private String originalAccessToken;
    private Long userID = null;           // snowflake
    private String clientID = null;
    private String installationID = null; // UUID
    private Long clientAppID;

    // 初始化对象构造器
    public InternalCallerContext(
            String distributeThreadID,
            String originalAccessToken,
            Long userID,
            String clientID,
            String installationID,
            Long clientAppID) {
        this.distributeThreadID = distributeThreadID;
        this.originalAccessToken = originalAccessToken;
        this.userID = userID;
        this.clientID = clientID;
        this.installationID = installationID;
        this.clientAppID = clientAppID;
    }

    public InternalCallerContext() {
    	this.distributeThreadID = Base64.getUrlEncoder().withoutPadding().encodeToString(UUIDUtil.convertUUIDToBytes(UUID.randomUUID()));
    }


    public String toBase64() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(JSON.toJSONBytes(this));
    }

    public String getDistributeThreadID() {
        return distributeThreadID;
    }

    public void setDistributeThreadID(
            String distributeThreadID) {
        this.distributeThreadID = distributeThreadID;
    }

    public String getOriginalAccessToken() {
        return originalAccessToken;
    }

    public void setOriginalAccessToken(
            String originalAccessToken) {
        this.originalAccessToken = originalAccessToken;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(
            Long userID) {
        this.userID = userID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getInstallationID() {
        return installationID;
    }

    public void setInstallationID(String installationID) {
        this.installationID = installationID;
    }

    public Long getClientAppID() {
        return clientAppID;
    }

    public void setClientAppID(Long clientAppID) {
        this.clientAppID = clientAppID;
    }
}
