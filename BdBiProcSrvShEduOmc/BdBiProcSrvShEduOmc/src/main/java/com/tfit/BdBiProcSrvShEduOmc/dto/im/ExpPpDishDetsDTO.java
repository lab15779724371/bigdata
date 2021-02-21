package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpPpDishDetsDTO {
	String time;
	ExpPpDishDets expPpDishDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpPpDishDets getExpPpDishDets() {
		return expPpDishDets;
	}
	public void setExpPpDishDets(ExpPpDishDets expPpDishDets) {
		this.expPpDishDets = expPpDishDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
