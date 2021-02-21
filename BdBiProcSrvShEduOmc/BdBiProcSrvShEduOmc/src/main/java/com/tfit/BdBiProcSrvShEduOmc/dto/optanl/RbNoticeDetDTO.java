package com.tfit.BdBiProcSrvShEduOmc.dto.optanl;

public class RbNoticeDetDTO {
	String time;
	RbNoticeDet rbNoticeDet;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public RbNoticeDet getRbNoticeDet() {
		return rbNoticeDet;
	}
	public void setRbNoticeDet(RbNoticeDet rbNoticeDet) {
		this.rbNoticeDet = rbNoticeDet;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
