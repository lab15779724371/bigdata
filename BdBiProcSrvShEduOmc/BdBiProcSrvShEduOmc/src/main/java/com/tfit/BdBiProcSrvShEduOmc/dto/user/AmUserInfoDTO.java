package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmUserInfoDTO {
	String time;
	AmUserInfo amUserInfo;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public AmUserInfo getAmUserInfo() {
		return amUserInfo;
	}
	public void setAmUserInfo(AmUserInfo amUserInfo) {
		this.amUserInfo = amUserInfo;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
