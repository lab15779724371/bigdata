package com.tfit.BdBiProcSrvShEduOmc.dto.ga;

public class ExpCaDishSupDetsDTO {
	String time;
	ExpCaDishSupDets expCaDishSupDets;
	long msgId;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ExpCaDishSupDets getExpCaDishSupDets() {
		return expCaDishSupDets;
	}
	public void setExpCaDishSupDets(ExpCaDishSupDets expCaDishSupDets) {
		this.expCaDishSupDets = expCaDishSupDets;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
