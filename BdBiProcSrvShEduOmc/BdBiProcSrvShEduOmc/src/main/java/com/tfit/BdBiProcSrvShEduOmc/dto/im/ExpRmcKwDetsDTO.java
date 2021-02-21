package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpRmcKwDetsDTO {
	String time;
	ExpRmcKwDets expRmcKwDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpRmcKwDets getExpRmcKwDets() {
		return expRmcKwDets;
	}
	public void setExpRmcKwDets(ExpRmcKwDets expRmcKwDets) {
		this.expRmcKwDets = expRmcKwDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
