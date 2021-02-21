package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpMatConfirmsDTO {
	String time;
	ExpMatConfirms expMatConfirms;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpMatConfirms getExpMatConfirms() {
		return expMatConfirms;
	}
	public void setExpMatConfirms(ExpMatConfirms expMatConfirms) {
		this.expMatConfirms = expMatConfirms;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
