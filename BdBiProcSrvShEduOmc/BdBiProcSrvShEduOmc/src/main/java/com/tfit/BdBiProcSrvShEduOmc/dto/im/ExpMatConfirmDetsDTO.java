package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpMatConfirmDetsDTO {
	String time;
	ExpMatConfirmDets expMatConfirmDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpMatConfirmDets getExpMatConfirmDets() {
		return expMatConfirmDets;
	}
	public void setExpMatConfirmDets(ExpMatConfirmDets expMatConfirmDets) {
		this.expMatConfirmDets = expMatConfirmDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
