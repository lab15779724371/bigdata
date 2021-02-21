package com.tfit.BdBiProcSrvShEduOmc.dto.apiDto;

import com.tfit.BdBiProcSrvShEduOmc.dto.IOTRspType;


public class OutCommonDto {
    private static long serialVersionUID = 7408790903212368997L;

    private String resCode = String.valueOf(IOTRspType.Success.getCode());

    private String resMsg = IOTRspType.Success.getMsg();

    private String version = "1.0";

    private Long timestamp = System.currentTimeMillis();
    
    private Long msgId;
    
    private String time;
    
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public static void setSerialversionuid(long serialversionuid) {
		serialVersionUID = serialversionuid;
	}
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public Long getMsgId() {
		return msgId;
	}
	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
