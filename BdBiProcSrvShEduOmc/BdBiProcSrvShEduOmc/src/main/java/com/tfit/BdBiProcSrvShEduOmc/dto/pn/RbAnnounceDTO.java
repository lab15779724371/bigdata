package com.tfit.BdBiProcSrvShEduOmc.dto.pn;

public class RbAnnounceDTO {
	String time;
	RbAnnounce rbAnnounce;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public RbAnnounce getRbAnnounce() {
		return rbAnnounce;
	}
	public void setRbAnnounce(RbAnnounce rbAnnounce) {
		this.rbAnnounce = rbAnnounce;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
