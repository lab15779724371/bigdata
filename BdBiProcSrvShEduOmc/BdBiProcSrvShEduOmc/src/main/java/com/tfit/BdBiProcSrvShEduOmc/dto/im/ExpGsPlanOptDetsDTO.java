package com.tfit.BdBiProcSrvShEduOmc.dto.im;

public class ExpGsPlanOptDetsDTO {
	String time;
	ExpGsPlanOptDets expGsPlanOptDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpGsPlanOptDets getExpGsPlanOptDets() {
		return expGsPlanOptDets;
	}
	public void setExpGsPlanOptDets(ExpGsPlanOptDets expGsPlanOptDets) {
		this.expGsPlanOptDets = expGsPlanOptDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
