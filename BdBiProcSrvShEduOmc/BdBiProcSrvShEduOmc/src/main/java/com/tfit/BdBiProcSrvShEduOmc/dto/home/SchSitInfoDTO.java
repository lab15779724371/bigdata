package com.tfit.BdBiProcSrvShEduOmc.dto.home;

public class SchSitInfoDTO {
	String time;
	SchSitInfo schSitInfo;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public SchSitInfo getSchSitInfo() {
		return schSitInfo;
	}
	public void setSchSitInfo(SchSitInfo schSitInfo) {
		this.schSitInfo = schSitInfo;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
