package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpSchKwDetsDTO {
	String time;
	ExpSchKwDets expSchKwDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpSchKwDets getExpSchKwDets() {
		return expSchKwDets;
	}
	public void setExpSchKwDets(ExpSchKwDets expSchKwDets) {
		this.expSchKwDets = expSchKwDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}