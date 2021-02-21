package com.tfit.BdBiProcSrvShEduOmc.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tfit.BdBiProcSrvShEduOmc.config.AppModConfig;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;
import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspVOParam;
import com.tfit.BdBiProcSrvShEduOmc.util.BCDTimeUtil;

/**
 * 接口返回统一结构（报错结构）
 * @author Administrator
 *
 * @param <T>
 */
@JsonInclude(Include.ALWAYS)
public class ApiResponseError<T> implements Serializable {

    private static final long serialVersionUID = 7408790903212368997L;
    private String resCode = String.valueOf(IOTRspType.Success.getCode());
    private String resMsg = IOTRspType.Success.getMsg();
    private String version = "1.0";
    private String time = BCDTimeUtil.convertNormalFrom(null);
    private Long msgId;
    //兼容原结构（2019-09-18前开发接口，采用才参数，之后也会同步写入）
    private IOTRspVOParam result;
    
    private T data;

    public ApiResponseError() {}

    public ApiResponseError(T obj) {
        this.data = obj;
    }

    @SuppressWarnings("unchecked")
	public ApiResponseError(String resCode, String resMsg) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.data = (T)new Object();
        this.result = new IOTRspVOParam(resMsg,Integer.valueOf(resCode));
    }
    
    public ApiResponseError(IOTRspType codeEnum, String resMsg) {
        this.resCode = codeEnum.getCode().toString();
        this.resMsg = resMsg;
        this.result = new IOTRspVOParam(resMsg,codeEnum.getCode());
    }

    public ApiResponseError<T> success() {

        return this;
    }

    public String getResCode() {
        return resCode;
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

    public ApiResponseError<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getVersion() {
        return version;
    }

    
	public IOTRspVOParam getResult() {
		return result;
	}

	public void setResult(IOTRspVOParam result) {
		this.result = result;
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