package com.tfit.BdBiProcSrvShEduOmc.dto.user;

public class AmSaveUserInfoDTO {
	String time;
	AmSaveUserInfo amSaveUserInfo;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public AmSaveUserInfo getAmSaveUserInfo() {
		return amSaveUserInfo;
	}
	public void setAmSaveUserInfo(AmSaveUserInfo amSaveUserInfo) {
		this.amSaveUserInfo = amSaveUserInfo;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
