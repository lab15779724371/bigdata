package com.tfit.BdBiProcSrvShEduOmc.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

/**
 * 接口返回统一结构
 * @author Administrator
 *
 * @param <T>
 */
@JsonInclude(Include.ALWAYS)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 7408790903212368997L;
    private String status = String.valueOf(IOTRspType.Success.getCode());
    private String resMsg = IOTRspType.Success.getMsg();
    private String version = "1.0";
    private String time = BCDTimeUtil.convertNormalFrom(null);
    private Long msgId;
    
    private T data;

    public ApiResponse() {}

    public ApiResponse(T obj) {
        this.data = obj;
    }

    @SuppressWarnings("unchecked")
	public ApiResponse(String resCode, String resMsg) {
        this.status = resCode;
        this.resMsg = resMsg;
        this.data = (T)new Object();
    }
    
    public ApiResponse(IOTRspType codeEnum, String resMsg) {
        this.status = codeEnum.getCode().toString();
        this.resMsg = resMsg;
    }

    public ApiResponse<T> success() {

        return this;
    }

    public String getResCode() {
        return status;
    }

    public String getResMsg() {
        return resMsg;
    }

    public String time() {
        return time;
    }

    public T getData() {
        return data;
    }

    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getVersion() {
        return version;
    }

	public Long getMsgId() {
		this.msgId = AppModConfig.msgId;
		// 消息ID
		AppModConfig.msgId++;
		// 消息id小于0判断
		AppModConfig.msgIdLessThan0Judge();
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = AppModConfig.msgId;
	}

}